import cv2, os
# print(cv2.__version__)

# Select video's path
PATH = './frame/testvideo.mov'
video = cv2.VideoCapture(PATH)

if not video.isOpened():
    print("Terminal could not open : ", PATH)
    exit(0)
    
length = int(video.get(cv2.CAP_PROP_FRAME_COUNT))
width = int(video.get(cv2.CAP_PROP_FRAME_WIDTH))
height = int(video.get(cv2.CAP_PROP_FRAME_HEIGHT))
fps = video.get(cv2.CAP_PROP_FPS)

print("\n-- Video information --")
print("length :", length)
print("width (pixel) :", width)
print("height (pixel) :", height)
print("fps : %.2f"%fps)
print()

# Create directory if it does not exists
try:
    if not os.path.exists(PATH[:-4]):
        os.makedirs(PATH[:-4])
except OSError:
    print("Error : creating directory. " + PATH[:-4])

count = 0
num = 0

while(video.isOpened()):
    ret, image = video.read()
    
    # resize image
    image = cv2.resize(image, (1080, 1080))
    
    if(int(video.get(1)) % 1 == 0):
        cv2.imwrite(PATH[:-4] + "/frame%04d.jpg" % count, image)
        
        # Case 1. print each frame
        print('Saved frame number :', str(int(video.get(1))))
        count += 1
        
        # Case 2. print progress percentage
        '''
        num += 1
        if (num >= int(length / 10)):
            print("Progress : {}%".format(int(count/length*100)))
            num = 0
        '''
    if count >= length:
        break
    
print("Process ended. \n")
video.release()
