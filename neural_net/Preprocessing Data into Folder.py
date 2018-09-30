import random
import os
import shutil

os.mkdir(os.getcwd() + '/dataset/train')
os.mkdir(os.getcwd() + '/dataset/test')
os.mkdir(os.getcwd() + '/dataset/val')

os.mkdir(os.getcwd() + '/dataset/train/recycle')
os.mkdir(os.getcwd() + '/dataset/train/trash')

os.mkdir(os.getcwd() + '/dataset/test/recycle')
os.mkdir(os.getcwd() + '/dataset/test/trash')

os.mkdir(os.getcwd() + '/dataset/val/recycle')
os.mkdir(os.getcwd() + '/dataset/val/trash')

recycle = os.getcwd() + '/dataset/recycle'
trash = os.getcwd() + '/dataset/trash'

recycle_img = [os.getcwd() + '/dataset/recycle/' + x for x in os.listdir(recycle)]
trash_img = [os.getcwd() + '/dataset/trash/' + x for x in os.listdir(trash)]
random.shuffle(recycle_img)
random.shuffle(trash_img)

recycle_train_img = recycle_img[:int(0.65 * len(recycle_img))]
recycle_val_img = recycle_img[int(0.65 * len(recycle_img)):int(0.8 * len(recycle_img))]
recycle_test_img = recycle_img[int(0.8 * len(recycle_img)):]

trash_train_img = trash_img[:int(0.65 * len(trash_img))]
trash_val_img = trash_img[int(0.65 * len(trash_img)):int(0.8 * len(trash_img))]
trash_test_img = trash_img[int(0.8 * len(trash_img)):]

for im in recycle_train_img:
    im_path = im.split('/')
    final = ""
    for i in range(0, len(im_path) - 2):
        final += im_path[i] + '/'
    final += 'train/recycle/' + im_path[len(im_path) - 1]
    os.rename(im, final)

for im in recycle_val_img:
    im_path = im.split('/')
    final = ""
    for i in range(0, len(im_path) - 2):
        final += im_path[i] + '/'
    final += 'val/recycle/' + im_path[len(im_path) - 1]
    os.rename(im, final)

for im in recycle_test_img:
    im_path = im.split('/')
    final = ""
    for i in range(0, len(im_path) - 2):
        final += im_path[i] + '/'
    final += 'test/recycle/' + im_path[len(im_path) - 1]
    os.rename(im, final)

for im in trash_train_img:
    im_path = im.split('/')
    final = ""
    for i in range(0, len(im_path) - 2):
        final += im_path[i] + '/'
    final += 'train/trash/' + im_path[len(im_path) - 1]
    os.rename(im, final)

for im in trash_val_img:
    im_path = im.split('/')
    final = ""
    for i in range(0, len(im_path) - 2):
        final += im_path[i] + '/'
    final += 'val/trash/' + im_path[len(im_path) - 1]
    os.rename(im, final)

for im in trash_test_img:
    im_path = im.split('/')
    final = ""
    for i in range(0, len(im_path) - 2):
        final += im_path[i] + '/'
    final += 'test/trash/' + im_path[len(im_path) - 1]
    os.rename(im, final)

os.rmdir(recycle)
os.rmdir(trash)
