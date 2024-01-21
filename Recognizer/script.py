from torchvision.io.image import read_image
from torchvision.models.segmentation import fcn_resnet50, FCN_ResNet50_Weights
from torchvision.transforms.functional import to_pil_image
from torchvision import transforms

import torch
import torchvision
import torch.nn.functional as torch_F
import torch
from torchvision import models

import sys
import base64
from PIL import Image
from io import BytesIO
import os


skyline_size = 512
clouds_size = 256

def recognize_image():
    image_as_base64 = sys.stdin.read()

    image_data = base64.b64decode(image_as_base64)

    input_image = Image.open(BytesIO(image_data))

    # Recognize skyline
    model_skyline = torchvision.models.segmentation.fcn_resnet50(weights = None, num_classes = 1, aux_loss = None)
    path = os.path.dirname(os.path.abspath(__file__))
    model_skyline.load_state_dict(torch.load(path + '/skyline_model.pth'))
    preprocess_skyline = transforms.Compose([
        transforms.Resize((skyline_size, skyline_size)),
        transforms.ToTensor(),
    ])
    device = "cuda" if torch.cuda.is_available() else "cpu"
    model_skyline.to(device).eval()

    input_image_vis_skyline = preprocess_skyline(input_image).unsqueeze(0).to(device)
    output_skyline = model_skyline(input_image_vis_skyline)['out']

    # Recognize clouds
    model_clouds = models.segmentation.deeplabv3_resnet50(weights = None, num_classes=1, aux_loss=None)
    model_clouds.load_state_dict(torch.load(path +'/clouds_model.pth'))
    preprocess_clouds = transforms.Compose([
        transforms.Resize((clouds_size, clouds_size)),
        transforms.ToTensor(),
    ])
    model_clouds.to(device).eval()

    input_image_vis_clouds = preprocess_clouds(input_image).unsqueeze(0).to(device)
    output_clouds = model_clouds(input_image_vis_clouds)['out']

    skyline_mask = (output_skyline > 0).float()
    clouds_mask = (output_clouds > 0).float()

    # Resize the masks to be the same size
    skyline_mask_resized = torch_F.interpolate(output_skyline, size=(clouds_size, clouds_size), mode='bilinear', align_corners=False)
    clouds_mask_resized = torch_F.interpolate(output_clouds, size=(clouds_size, clouds_size), mode='bilinear', align_corners=False)

    # Calculate the ratio
    skyline_pixels = int(torch.sum(skyline_mask_resized > 0).item())
    clouds_pixels = int(torch.sum(clouds_mask_resized > 0).item())
    # print(skyline_pixels)
    # print(clouds_pixels)

    ratio = clouds_pixels / skyline_pixels if skyline_pixels != 0 else 0
    if ratio > 1:
        ratio = 1

    # print(f"The ratio of skyline pixels to cloud pixels is {ratio}")

    """ # Visualize the masks
    plt.figure(figsize=(10, 5))

    plt.subplot(1, 2, 1)
    plt.imshow(skyline_mask[0, 0].cpu().detach().numpy(), cmap='gray')
    plt.title('Skyline Mask')

    plt.subplot(1, 2, 2)
    plt.imshow(clouds_mask[0, 0].cpu().detach().numpy(), cmap='gray')
    plt.title('Clouds Mask')

    plt.show() """
    print(ratio)
    return ratio

recognize_image()





