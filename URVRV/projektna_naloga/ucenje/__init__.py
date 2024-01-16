import numpy as np
import os
import json
from PIL import Image
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
from torchvision import models
from torchvision import transforms
import matplotlib.pyplot as plt
from typing import List
from torch.utils.data import Dataset as DS


class InputStream:
    def __init__(self, data):
        self.data = data
        self.i = 0

    def read(self, size):
        out = self.data[self.i:self.i + size]
        self.i += size
        return int(out, 2)
def bytes2bit(data):
    """ get bit string from bytes data"""
    return ''.join([str(access_bit(data, i)) for i in range(len(data) * 8)])

def access_bit(data, num):
    """ from bytes array to bits by num position"""
    base = int(num // 8)
    shift = 7 - int(num % 8)
    return (data[base] & (1 << shift)) >> shift

def rle_to_mask(rle: List[int], height: int, width: int) -> np.array:
    """
    Converts rle to image mask
    Args:
        rle: your long rle
        height: original_height
        width: original_width

    Returns: np.array
    """

    rle_input = InputStream(bytes2bit(rle))

    num = rle_input.read(32)
    word_size = rle_input.read(5) + 1
    rle_sizes = [rle_input.read(4) + 1 for _ in range(4)]
    # print('RLE params:', num, 'values,', word_size, 'word_size,', rle_sizes, 'rle_sizes')

    i = 0
    out = np.zeros(num, dtype=np.uint8)
    while i < num:
        x = rle_input.read(1)
        j = i + 1 + rle_input.read(rle_sizes[rle_input.read(2)])
        if x:
            val = rle_input.read(word_size)
            out[i:j] = val
            i = j
        else:
            while i < j:
                val = rle_input.read(word_size)
                out[i] = val
                i += 1

    image = np.reshape(out, [height, width, 4])[:, :, 3]
    return image

def prepare_dataset2():
    with open('data/urvrv-podatki5.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    i = 0
 
    for image in data:
        mask_info = image['tag']
        
        for mask in image['tag']: 
            binary_mask = rle_to_mask(mask['rle'], mask['original_height'], mask['original_width'])
                

        Image.fromarray(binary_mask).save(f"outputfolder/masks/mask_{i}.png") 
          

        full_image_name = image['image'].split('/')[-1]
        image_name = full_image_name.split('-')[-1]
        input_image = Image.open(f"data/images/{image_name}")
        input_image.save(f"outputfolder/images/image_{i}.png")
        i = i + 1 
        

def checkIfFileExists(directory_name):
    data_folder = "data"
    if os.path.exists(data_folder) and os.path.isdir(data_folder):
        print(f"The directory '{data_folder}' exists.")
    else:
        print(f"The directory '{data_folder}' does not exist.")
    return


class DataSet(DS):
    def __init__(self, root, transform=None):
        self.root = root
        self.transform = transform
        self.data = self.load_data()

    def load_data(self):
        data = []
        with open( 'data/urvrv-output.json', 'r') as f:
            urvrv_output = json.load(f)

        for i, item in enumerate(urvrv_output):
            image_name = item['image_name']
            image_path = os.path.join(self.root, 'images', f'image_{i}.png')
            mask_path = os.path.join(self.root, 'masks', f'mask_{i}.png')
            cloudiness_ratio = item['Ratio of Clouds to Skyline']
            

            data.append({
                'image_path': image_path,
                'mask_path': mask_path
            })


        return data

    def __len__(self):
        return len(self.data)

    def __getitem__(self, idx):
        item = self.data[idx]
        image_path = item['image_path']
        mask_path = item['mask_path']

        try:
            input_image = Image.open(image_path)
            mask = Image.open(mask_path).convert("L")
        except OSError as e:
            print(f"Error opening image at {image_path}: {e}")
            return None

        mask = np.array(mask)

        mask[mask == -1] = 0
        mask = Image.fromarray(mask)

        mask_transform = transforms.Compose([
            transforms.Resize((256, 256)),
            transforms.ToTensor(),
        ])

        if self.transform is not None:
            input_image = self.transform(input_image)
            mask = mask_transform(mask)
        #normalize = transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
        #input_image = normalize(input_image)
        return input_image, mask


def train_model(model, train_loader, criterion, optimizer, num_epochs):
    model.train()
    print("about to train model")
    for epoch in range(num_epochs):
        running_loss = 0.0
        print("training model")
        for inputs, masks in train_loader:
            optimizer.zero_grad()
            outputs = model(inputs)['out']

            loss = criterion(outputs.view(-1), masks.view(-1))
            loss.backward()
            optimizer.step()
            running_loss += loss.item()

        average_loss = running_loss / len(train_loader)
        print(f'Epoch {epoch + 1}/{num_epochs}, Loss: {average_loss}')

    torch.save(model.state_dict(), 'model_state50.pth')
    print('Training finished.')


def startTraining(num_epochs: int):
    print("started training")
    
    model = models.segmentation.deeplabv3_resnet50(weights = None, num_classes=1, aux_loss=None)
    
    def initialize_weights(m):
        if isinstance(m, nn.Conv2d) or isinstance(m, nn.ConvTranspose2d):
            nn.init.kaiming_normal_(m.weight, mode='fan_out', nonlinearity='relu')
            if m.bias is not None:
                nn.init.constant_(m.bias, 0)
                
    model.apply(initialize_weights)
    
    model.train()

    model.classifier[4] = nn.Conv2d(256, 1, kernel_size=(1, 1), stride=(1, 1))

    criterion = nn.BCEWithLogitsLoss()
    optimizer = optim.Adam(model.parameters(), lr=0.001)

    transform = transforms.Compose([
        transforms.Resize((256, 256)),
        transforms.ToTensor(),

    ])

    train_dataset = DataSet(root='outputfolder', transform=transform)
    train_loader = DataLoader(train_dataset, batch_size=8, shuffle=True, num_workers=0)

    print("ending the start training")
    train_model(model, train_loader, criterion, optimizer, num_epochs)

def segment_and_visualize_last30(model, folder_path):
    image_files = sorted([f for f in os.listdir(folder_path) if f.endswith(('.jpg', '.png'))])
    last30_images = image_files

    preprocess = transforms.Compose([
        transforms.Resize((256, 256)),
        transforms.ToTensor(),
    ])
    device = "cuda" if torch.cuda.is_available() else "cpu"
    model.to(device).eval()

    with torch.no_grad():
        for image_file in last30_images:
            image_path = os.path.join(folder_path, image_file)
            input_image = Image.open(image_path)
            input_image_vis = preprocess(input_image).unsqueeze(0)

            output = model(input_image_vis)['out']

            predicted_mask = torch.sigmoid(output).argmax(0)
            predicted_mask = (output > 0).float()
            print(f"predicted mask \n {predicted_mask}")
            binary_mask = (predicted_mask.squeeze().cpu().numpy())

            input_image = np.rot90(input_image, k=-1)
            plt.figure(figsize=(10, 5))
            plt.subplot(1, 2, 1)
            plt.imshow(input_image)
            plt.title("Original Image")

            plt.subplot(1, 2, 2)
            plt.imshow(binary_mask, cmap='gray')  
            plt.title("Predicted Cloud Mask")

            plt.show()

