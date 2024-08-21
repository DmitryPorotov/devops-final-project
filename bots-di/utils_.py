from inspect import currentframe, getframeinfo
import traceback

def print_file_lineno_error(e: Exception):
    frame_info = getframeinfo(currentframe().f_back)
    print("File: {}, line: {}, error: {}".format(frame_info.filename, frame_info.lineno, e))
    if e.__traceback__:
        print("Traceback:")
        for s in traceback.format_tb(e.__traceback__):
            print(s)