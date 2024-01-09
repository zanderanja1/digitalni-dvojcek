from typing import List
import numpy as np
import json
from PIL import Image

class InputStream:
    def __init__(self, data):
        self.data = data
        self.i = 0

    def read(self, size):
        out = self.data[self.i:self.i + size]
        self.i += size
        return int(out, 2)


def access_bit(data, num):
    """ from bytes array to bits by num position"""
    base = int(num // 8)
    shift = 7 - int(num % 8)
    return (data[base] & (1 << shift)) >> shift


def bytes2bit(data):
    """ get bit string from bytes data"""
    return ''.join([str(access_bit(data, i)) for i in range(len(data) * 8)])


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


with open('C:\\Users\\Jakob\\Downloads\\urvrv-podatki5.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

output_data = []
for image in data:
    skyline_pixels = 0
    clouds_pixels = 0
    
    for mask in image['tag']:
        binary_mask = rle_to_mask(mask['rle'], mask['original_height'], mask['original_width'])
        """ Image.fromarray(binary_mask).show() """
        if mask['brushlabels'][0] == "Skyline":
            skyline_pixels += np.sum(binary_mask)
        elif mask['brushlabels'][0] == "Clouds":
            clouds_pixels += np.sum(binary_mask)
    if clouds_pixels > 0 and clouds_pixels < skyline_pixels: 
        ratio = clouds_pixels / skyline_pixels
    elif clouds_pixels > skyline_pixels:
        ratio = 1
    else:
        ratio = 0 

    full_image_name = image['image'].split('/')[-1]
    image_name = full_image_name.split('-')[-1]
    output_data.append({
            'id': image['id'],
            'image_name': image_name,
            'Skyline pixels': int(skyline_pixels),
            'Clouds pixels': int(clouds_pixels),
            'Weather': image['weather'],
            'Date': image['date'][0]['datetime'],
            'Ratio of Clouds to Skyline': ratio
        })

with open('C:\\Users\\Jakob\\Downloads\\urvrv-output.json', 'w', encoding='utf-8') as f:
    json.dump(output_data, f, ensure_ascii=False, indent=4)
    