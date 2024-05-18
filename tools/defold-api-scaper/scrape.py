from bs4 import BeautifulSoup
from urllib.request import urlopen
from lxml import etree

from content_parser import ContentParser

url = "https://defold.com/ref/stable/gui/"
page = urlopen(url)
html = page.read().decode("utf-8")
soup = BeautifulSoup(html, "html.parser")

content = soup.find_all('div', attrs={'class': 'apicontent'})
str_cont = str(content)
cont = etree.HTML(str(content))
h2s = cont.xpath('//h2')
parts = []
for idx, h2 in enumerate(h2s):
    parent = h2.getparent()
    p = {'name': h2.text, 's': parent.index(h2)}
    try:
        if h2s[idx + 1].getparent() == parent:
            p['e'] = parent.index(h2s[idx + 1])
    except IndexError:
        pass

    p['parent'] = parent

    def is_in_range(h4_):
        if h4_.getparent() != p['parent']:
            return False
        if 'e' in p:
            return p['s'] < p['parent'].index(h4_) < p['e']
        else:
            return p['s'] < p['parent'].index(h4_)


    p['h2'] = h2

    p['is_in_range'] = is_in_range
    parts.append(p)

h4s = cont.xpath('//h4')

for h4 in h4s:
    for p in parts:
        if p['is_in_range'](h4):
            if 'content' not in p:
                p['content'] = []
            if p['name'] == 'Functions':
                p['content'].append(ContentParser.read_function_data(h4))
            elif p['name'] == 'Constants':
                p['content'].append(ContentParser.read_constant_data(h4))


print('---@module {}'.format('gui'))
for p in parts:
    if p['name'] == 'Functions':
        for c in p['content']:
            if '.' in c['function_name']:
                func_name = c['function_name'].split('.')[1]
                out = '---@field {} fun('.format(func_name)
                len_pr = len(c['params'])
                for i, pr in enumerate(c['params']):
                    name = pr['name']
                    out += name + ': ' + ' | '.join(pr['types'])
                    if i < len_pr - 1:
                        out += ', '
                out += ') {}'.format(c['description_text'].replace('\n', ' ') if c['description_text'] is not None else '')
                print(out)
    if p['name'] == 'Constants':
        for c in p['content']:
            print('---@field {} number {}'.format( c['const_name'].split('.')[1], c['description_text']))
a = 0
