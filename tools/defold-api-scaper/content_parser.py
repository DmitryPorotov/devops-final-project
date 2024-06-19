from typing import TypedDict, List
from lxml.etree import Element
import re


class FunctionParams(TypedDict):
    name: str
    types: List[str]
    descr: str


class FunctionData(TypedDict):
    function_name: str
    description_text: str | None
    params: List[FunctionParams]
    returns: List[FunctionParams] | None


class ConstantData(TypedDict):
    const_name: str
    description_text: str | None


class ContentParser:
    @staticmethod
    def read_function_data(header_el: Element) -> FunctionData:
        retval: FunctionData = {'function_name': header_el.text.replace('(', '').replace(')', '')}
        main_description_el = header_el.getnext().getnext()
        retval['description_text'] = str(main_description_el.xpath('string()')).strip()
        params_el = ContentParser._get_params_el(main_description_el)
        retval['params'] = ContentParser._read_function_params(params_el)
        returns_el = ContentParser._get_returns_el(params_el)
        if returns_el is not None:
            retval['returns'] = ContentParser._read_function_params(returns_el)
        return retval

    @staticmethod
    def _get_params_el(el: Element):
        if el.xpath('string()').strip() == 'PARAMETERS':
            return el.getnext()
        else:
            return ContentParser._get_params_el(el.getnext())

    @staticmethod
    def _get_returns_el(el: Element, _depth=1):
        if _depth > 3:
            return None
        if el.xpath('string()').strip() == 'RETURNS':
            return el.getnext()
        else:
            return ContentParser._get_returns_el(el.getnext(), _depth + 1)

    @staticmethod
    def _read_function_params(table: Element) -> List[FunctionParams]:
        rows = table.xpath('tr')
        l: List[FunctionParams] = []
        for r in rows:
            tds = r.xpath('td')
            types_str = tds[1].xpath('string()')
            l.append({
                'name': tds[0].xpath('string()')
                .replace('[', '')
                .replace(']', '')
                .replace('-', '_')
                .replace('repeat', 'repeat_'),
                'types': ContentParser._read_function_as_param(types_str) if 'function(' in types_str else
                (ContentParser._type_substitute(t) for t in types_str.split(', ')),
                'descr': ('[optional]' if ('[' in tds[0].xpath('string()')) else '') +
                         (tds[2].xpath('string()') if tds[2].text is not None else '').strip()
                .replace('\n\n', '\n')
            })
        return l

    @staticmethod
    def _read_function_as_param(func: str) -> List[str]:
        parts = func.split('), ')
        if len(parts) == 1:
            return parts
        else:
            return [parts[0] + ')', *parts[1:]]

    @staticmethod
    def _type_substitute(type_: str) -> str:
        if type_ == 'bool':
            return 'boolean'
        elif type_.startswith('vmath.'):
            return type_.replace('vmath.', '')
        elif type_ == 'array':
            return 'table'
        elif type_ == 'integer':
            return 'number'
        elif type_ == 'float':
            return 'number'
        else:
            return type_

    @staticmethod
    def read_constant_data(header_el: Element) -> ConstantData:
        retval = {
            'const_name': header_el.text,
            'description_text': header_el.getnext().text
        }
        return retval
