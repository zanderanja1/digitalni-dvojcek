import ucenje as uc
import torch
from torchvision import models


uc.startTraining(10)
uc.prepare_dataset2()


def load_model(model, model_path):
    
    model.load_state_dict(torch.load(model_path))
    print('Model state dictionary loaded.')

model = models.segmentation.deeplabv3_resnet50(weights = None, num_classes=1, aux_loss=None)
load_model(model, 'model_state.pth')
uc.segment_and_visualize_last30(model, "outputfolder/images")


data_folder = "data"

output_folder = "outputdata"
json_file_1 = "urvrv-output.json"
json_file_2 = "urvrv-podatki5.json"



