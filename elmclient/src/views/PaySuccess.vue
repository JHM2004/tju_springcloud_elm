# 导入必要的库
import paddle
import paddle.nn as nn
import numpy as np
import os
from datetime import datetime
from paddle.io import DataLoader, Dataset
import cv2
from visualdl import LogWriter

# 训练数据列表路径
train_data_list_path = 'dataset/train_list.txt'
# 测试数据列表路径
test_data_list_path = 'dataset/test_list.txt'
# 词汇表路径
voc_path = 'dataset/vocabulary.txt'
# 模型保存的路径
save_model = 'models/'
# 每一批数据大小
batch_size = 32
# 预训练模型路径
pretrained_model = None
# 训练轮数
num_epoch = 20
# 初始学习率大小
learning_rate = 1e-3
# 日志记录器
writer = LogWriter(logdir='log')

# 数据集类
class CustomDataset(Dataset):
    def __init__(self, data_list_path, vocabulary_path, img_height=32, is_data_enhance=True):
        super(CustomDataset, self).__init__()
        self.data_list_path = data_list_path
        self.img_height = img_height
        self.is_data_enhance = is_data_enhance
        
        # 加载词汇表
        with open(vocabulary_path, 'r', encoding='utf-8') as f:
            self.vocabulary = [line.strip() for line in f.readlines()]
        
        # 加载数据列表
        with open(data_list_path, 'r', encoding='utf-8') as f:
            self.data_list = [line.strip() for line in f.readlines()]
    
    def __getitem__(self, idx):
        # 解析数据行
        img_path, label = self.data_list[idx].split('\t')
        
        # 读取图像
        img = cv2.imread(img_path, cv2.IMREAD_GRAYSCALE)
        if img is None:
            return self.__getitem__((idx + 1) % len(self))
        
        # 调整图像大小
        img_height = self.img_height
        img_width = int(img.shape[1] * (img_height / img.shape[0]))
        img = cv2.resize(img, (img_width, img_height))
        
        # 数据增强
        if self.is_data_enhance:
            if np.random.random() < 0.5:
                img = self._random_blur(img)
            if np.random.random() < 0.5:
                img = self._random_noise(img)
        
        # 归一化
        img = img.astype('float32') / 255.0
        img = img.reshape((1, img_height, img_width))
        
        # 标签转换为索引
        label = [self.vocabulary.index(c) for c in label]
        
        return img, np.array(label).astype('int64')
    
    def __len__(self):
        return len(self.data_list)
    
    def _random_blur(self, img):
        kernel_size = np.random.choice([3, 5])
        img = cv2.GaussianBlur(img, (kernel_size, kernel_size), 0)
        return img
    
    def _random_noise(self, img):
        noise = np.random.normal(0, 1, img.shape)
        img = img + noise * 0.1
        img = np.clip(img, 0, 255)
        return img.astype(np.uint8)

# 数据整理函数
def collate_fn(batch):
    # 分离图像和标签
    images = [item[0] for item in batch]
    labels = [item[1] for item in batch]
    
    # 获取图像宽度
    widths = [image.shape[2] for image in images]
    max_width = max(widths)
    
    # 填充图像到相同宽度
    padded_images = []
    for image in images:
        padding_width = max_width - image.shape[2]
        padded_image = np.pad(image, ((0, 0), (0, 0), (0, padding_width)), 'constant')
        padded_images.append(padded_image)
    
    # 转换为tensor
    images = paddle.to_tensor(np.array(padded_images))
    
    # 获取标签长度
    label_lengths = [len(label) for label in labels]
    max_label_length = max(label_lengths)
    
    # 填充标签到相同长度
    padded_labels = []
    for label in labels:
        padding_length = max_label_length - len(label)
        padded_label = np.pad(label, (0, padding_length), 'constant', constant_values=0)
        padded_labels.append(padded_label)
    
    labels = paddle.to_tensor(np.array(padded_labels))
    label_lengths = paddle.to_tensor(np.array(label_lengths))
    input_lengths = paddle.to_tensor(np.array([max_width] * len(batch)))
    
    return images, labels, input_lengths, label_lengths

# CTC解码器
def ctc_greedy_decoder(probs, vocabulary):
    """CTC贪婪解码器"""
    raw_str = np.argmax(probs, axis=1)
    # 删除重复的标签
    result = []
    prev = -1
    for c in raw_str:
        if c != prev and c != len(vocabulary):  # 跳过空白符
            result.append(vocabulary[c])
        prev = c
    return ''.join(result)

def label_to_string(label, vocabulary):
    """标签转换为字符串"""
    result = []
    for l in label:
        if l == len(vocabulary):  # 空白符
            continue
        result.append(vocabulary[l])
    return ''.join(result)

def cer(pred, label):
    """计算字符错误率"""
    dist = levenshtein(pred, label)
    return dist

def levenshtein(s1, s2):
    """计算编辑距离"""
    if len(s1) < len(s2):
        return levenshtein(s2, s1)
    if len(s2) == 0:
        return len(s1)
    
    previous_row = range(len(s2) + 1)
    for i, c1 in enumerate(s1):
        current_row = [i + 1]
        for j, c2 in enumerate(s2):
            insertions = previous_row[j + 1] + 1
            deletions = current_row[j] + 1
            substitutions = previous_row[j] + (c1 != c2)
            current_row.append(min(insertions, deletions, substitutions))
        previous_row = current_row
    
    return previous_row[-1]

class Model(nn.Layer):
    def __init__(self, vocabulary, image_height, channel=1):
        super(Model, self).__init__()
        # 获取字符表大小
        self.vocab_size = len(vocabulary)
        
        # CNN结构
        self.conv1 = nn.Conv2D(in_channels=channel, out_channels=32, kernel_size=3, stride=1, padding=1)
        self.bn1 = nn.BatchNorm2D(32)
        self.conv2 = nn.Conv2D(in_channels=32, out_channels=64, kernel_size=3, stride=1, padding=1)
        self.bn2 = nn.BatchNorm2D(64)
        self.conv3 = nn.Conv2D(in_channels=64, out_channels=128, kernel_size=3, stride=1, padding=1)
        self.bn3 = nn.BatchNorm2D(128)
        self.conv4 = nn.Conv2D(in_channels=128, out_channels=256, kernel_size=3, stride=1, padding=1)
        self.bn4 = nn.BatchNorm2D(256)
        self.pool = nn.MaxPool2D(kernel_size=2, stride=2)
        
        # 计算LSTM输入维度
        self.lstm_input_size = 256 * (image_height // 16)  # 经过4次池化，高度变为原来的1/16
        
        # BiLSTM结构
        self.lstm = nn.LSTM(
            input_size=self.lstm_input_size,
            hidden_size=256,
            num_layers=2,
            direction='bidirectional',
            dropout=0.2
        )
        
        # 全连接层
        self.fc = nn.Linear(in_features=512, out_features=self.vocab_size + 1)  # +1 为空白符
        
    def forward(self, x):
        # CNN特征提取
        x = self.pool(paddle.nn.functional.relu(self.bn1(self.conv1(x))))  # [B, 32, H/2, W/2]
        x = self.pool(paddle.nn.functional.relu(self.bn2(self.conv2(x))))  # [B, 64, H/4, W/4]
        x = self.pool(paddle.nn.functional.relu(self.bn3(self.conv3(x))))  # [B, 128, H/8, W/8]
        x = self.pool(paddle.nn.functional.relu(self.bn4(self.conv4(x))))  # [B, 256, H/16, W/16]
        
        # 调整维度 [B, C, H, W] -> [W, B, C*H]
        x = paddle.transpose(x, perm=[3, 0, 1, 2])  # [W, B, C, H]
        w, b, c, h = x.shape
        x = paddle.reshape(x, shape=[w, b, c * h])  # [W, B, C*H]
        
        # BiLSTM序列识别
        x, (_, _) = self.lstm(x)  # [W, B, 2*H]
        
        # 全连接层预测字符
        output = self.fc(x)  # [W, B, vocab_size+1]
        
        return output

def train():
    # 获取训练数据
    train_dataset = CustomDataset(train_data_list_path, voc_path, img_height=32)
    train_loader = DataLoader(dataset=train_dataset, batch_size=batch_size, collate_fn=collate_fn, shuffle=True)
    # 获取测试数据
    test_dataset = CustomDataset(test_data_list_path, voc_path, img_height=32, is_data_enhance=False)
    test_loader = DataLoader(dataset=test_dataset, batch_size=batch_size, collate_fn=collate_fn)
    # 获取模型
    model = Model(train_dataset.vocabulary, image_height=train_dataset.img_height, channel=1)
    
    # 设置优化方法
    boundaries = [30, 100, 200]
    lr = [0.1 ** l * learning_rate for l in range(len(boundaries) + 1)]
    scheduler = paddle.optimizer.lr.PiecewiseDecay(boundaries=boundaries, values=lr, verbose=False)
    optimizer = paddle.optimizer.Adam(parameters=model.parameters(),
                                    learning_rate=scheduler,
                                    weight_decay=paddle.regularizer.L2Decay(1e-4))
    
    # 获取损失函数
    ctc_loss = paddle.nn.CTCLoss()
    
    # 加载预训练模型
    if pretrained_model is not None:
        model.set_state_dict(paddle.load(os.path.join(pretrained_model, 'model.pdparams')))
        optimizer.set_state_dict(paddle.load(os.path.join(pretrained_model, 'optimizer.pdopt')))

    train_step = 0
    test_step = 0
    
    # 开始训练
    for epoch in range(num_epoch):
        model.train()  # 设置为训练模式
        for batch_id, (inputs, labels, input_lengths, label_lengths) in enumerate(train_loader()):
            # 前向传播
            out = model(inputs)
            # 计算损失
            input_lengths = paddle.full(shape=[batch_size], fill_value=out.shape[0], dtype='int64')
            loss = ctc_loss(out, labels, input_lengths, label_lengths)
            
            # 反向传播
            loss.backward()
            optimizer.step()
            optimizer.clear_grad()
            
            # 打印训练信息
            if batch_id % 100 == 0:
                print('[%s] Train epoch %d, batch %d, loss: %f' % 
                      (datetime.now(), epoch, batch_id, loss))
                writer.add_scalar('Train loss', loss, train_step)
                train_step += 1
        
        # 评估模型
        if (epoch + 1) % 5 == 0 or epoch + 1 == num_epoch:
            model.eval()  # 设置为评估模式
            cer = evaluate(model, test_loader, train_dataset.vocabulary)
            print('[%s] Test epoch %d, 错字率: %f' % 
                  (datetime.now(), (epoch + 1), cer))
            writer.add_scalar('Test cer', cer, test_step)
            test_step += 1
            model.train()  # 恢复训练模式
        
        # 记录学习率
        writer.add_scalar('Learning rate', scheduler.last_lr, epoch)
        scheduler.step()
        
        # 保存模型
        paddle.save(model.state_dict(), os.path.join(save_model, 'model.pdparams'))
        paddle.save(optimizer.state_dict(), os.path.join(save_model, 'optimizer.pdopt'))

def evaluate(model, test_loader, vocabulary):
    model.eval()  # 设置为评估模式
    cer_result = []
    
    with paddle.no_grad():  # 关闭梯度计算
        for batch_id, (inputs, labels, _, _) in enumerate(test_loader()):
            # 执行识别
            outs = model(inputs)
            outs = paddle.transpose(outs, perm=[1, 0, 2])
            outs = paddle.nn.functional.softmax(outs)
            
            # 解码获取识别结果
            labelss = []
            out_strings = []
            for out in outs:
                out_string = ctc_greedy_decoder(out, vocabulary)
                out_strings.append(out_string)
            for i, label in enumerate(labels):
                label_str = label_to_string(label, vocabulary)
                labelss.append(label_str)
            
            # 计算字错率
            for out_string, label in zip(*(out_strings, labelss)):
                c = cer(out_string, label) / float(len(label))
                cer_result.append(c)
    
    cer_result = float(np.mean(cer_result))
    return cer_result

if __name__ == '__main__':
    train() 