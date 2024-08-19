from inspect import currentframe, getframeinfo

def print_file_lineno_error(e: Exception):
    frame_info = getframeinfo(currentframe().f_back.f_back)
    print("file: {}, line: {}, error: {}".format(frame_info.filename, frame_info.lineno, e))