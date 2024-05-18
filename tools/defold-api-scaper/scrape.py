from bs4 import BeautifulSoup
from urllib.request import urlopen
from lxml import etree

from content_parser import ContentParser

url = "https://defold.com/ref/stable/go/"
page = urlopen(url)
html = page.read().decode("utf-8")
soup = BeautifulSoup(html, "html.parser")

content = soup.find_all('div', attrs={'class': 'apicontent'})
cont = etree.HTML(str(content))
h2s = cont.xpath('//h2')
parts = []
for idx, h2 in enumerate(h2s):
    p = {'name': h2.text, 's': h2.sourceline}
    try:
        p['e'] = h2s[idx + 1].sourceline
    except IndexError:
        pass

    def is_in_range(h4_):
        if 'e' in p:
            return p['s'] < h4_.sourceline < p['e']
        else:
            return p['s'] < h4_.sourceline

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


def find_module_name(parts_):
    for p in parts_:
        if p['name'] == 'Functions':
            for c in p['content']:
                if '.' in c['function_name']:
                    return c['function_name'].split('.')[0]


print('---@module {}'.format(find_module_name(parts)))
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
                out += ')'
                if 'returns' in c:
                    out += ': ' + (' | '.join(c['returns'][0]['types']))
                out += ' {}'.format(c['description_text']
                                    .replace('\n', ' ') if c['description_text'] is not None else '')

                print(out)
    if p['name'] == 'Constants':
        for c in p['content']:
            print('---@field {} number {}'.format(c['const_name'].split('.')[1], c['description_text'].replace('\n', ' ')))
