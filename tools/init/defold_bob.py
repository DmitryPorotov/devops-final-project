import urllib.request
import http.client
import re
from datetime import datetime


def download_bob():
    page = urllib.request.urlopen('https://github.com/defold/defold/releases')  # type: http.client.HTTPResponse
    content = page.read()
    bob_url = re.search("(defold/defold/releases/download/\d+\.\d+\.\d+/bob.jar)", str(content))  # type: re.Match
    jar_url = bob_url.group()

    last_update = datetime(1970, 1, 1)

    def rep_hook(blk_num, b_read, b_total):
        nonlocal last_update
        now = datetime.now()
        delta = now - last_update
        if delta.seconds > 2 or blk_num * b_read >= b_total:
            print('Read {:.1f}Mb of total {:.1f}Mb'.format(b_read * blk_num / 2 ** 20, b_total / 2 ** 20))
            last_update = now

    urllib.request.urlretrieve('https://github.com/' + jar_url, './.cache/bob.jar', rep_hook)
    return jar_url


if __name__ == '__main__':
    download_bob()
