#!/usr/bin/python3

from bs4 import BeautifulSoup
from urllib.request import urlopen
from lxml import etree
from lxml.etree import Element
from typing import List, Dict, TypedDict, Callable

from content_parser import ContentParser, FunctionData, ConstantData

base_url = "https://defold.com"


class DocPart(TypedDict):
    s: int
    e: int | None
    name: str
    content: List[FunctionData] | List[ConstantData]
    is_in_range: Callable[[Element], bool]


def get_soup_for_url(url: str) -> BeautifulSoup:
    page = urlopen(url)
    html = page.read().decode("utf-8")
    return BeautifulSoup(html, "html.parser")


def get_api_urls() -> List[str]:
    soup = get_soup_for_url(base_url + "/ref/stable/go/")
    content = soup.find_all('div', attrs={'class': 'apimenu'})
    cont = etree.HTML(str(content))
    labels = cont.xpath('//div/ul/label')
    defold_menu_ul = None
    for label in labels:
        if label.xpath('string()').strip() == 'Defold':
            defold_menu_ul = label.getnext()  # type: Element
            break
    if defold_menu_ul is None:
        raise Exception('Could not find the Defold API sub menu.')
    anchors = defold_menu_ul.xpath('li/a')  # type: List[Element]
    return [a_.attrib['href'] for a_ in anchors]


def group_functions(functions: List[FunctionData]) -> Dict[str, List[FunctionData]]:
    retval: Dict[str, List[FunctionData]] = dict()
    for f in functions:
        if '.' not in f['function_name']:
            continue
        if f["function_name"] not in retval:
            retval[f["function_name"]] = []
        retval[f["function_name"]].append(f)
    return retval


def get_api(api_url: str):
    soup = get_soup_for_url(base_url + api_url)

    content = soup.find_all('div', attrs={'class': 'apicontent'})
    cont = etree.HTML(str(content))
    h2s = cont.xpath('//h2')
    parts: List[DocPart] = []
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
                        return '.'.join(c['function_name'].split('.')[:-1])

    module_name = find_module_name(parts)

    if module_name is None:
        return

    constants = None
    functions = None
    print('---@module {}'.format(module_name))
    for p in parts:
        if p['name'] == 'Functions':
            functions = p
        elif p['name'] == 'Constants':
            constants = p

    if constants is not None:
        for c in constants['content']:  # type: ConstantData
            print('---@field {} number {}'.format(c['const_name'].split('.')[-1], c['description_text']
                                                  .replace('\n', ' ') if c['description_text'] is not None else ''))
        print()

    if functions is not None:
        groups = group_functions(functions['content'])
        for func_name, gr in groups.items():
            last = gr[-1]
            rest = gr[:-1]
            print()
            print('---')
            print('--- ' + last['description_text']
                  .replace('\n', '\n--- ') if last['description_text'] is not None else '')
            for r in rest:
                print('---@overload fun({}): {} {}'.format(
                    ', '.join(('{}: {}'.format(pr_['name'], ' | '.join(pr_["types"]))) for pr_ in r['params']),
                    (' | '.join(r["returns"][0]['types']) if 'returns' in r else 'void'),
                    r['description_text']
                    .replace('\n', ' ') if r['description_text'] is not None else ''
                ))
            for pr_ in last['params']:
                print('---@param {} {} {}'.format(pr_["name"], ' | '.join(pr_["types"]),
                                                  pr_["descr"].replace('\n', ' ')))
            if 'returns' in last:
                print('---@return {} {}'.format(' | '.join(last['returns'][0]['types']),
                                                last['returns'][0]['descr']))
            print('function {}({}) end'.format(last['function_name'], ', '
                                               .join(pr_['name'] for pr_ in last['params'])))


urls = get_api_urls()

for url in urls:
    get_api(url)
    print()

# get_api('/ref/stable/window')
