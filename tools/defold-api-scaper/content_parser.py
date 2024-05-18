class ContentParser:
    @staticmethod
    def read_function_data(header_el):
        retval = {'function_name': header_el.text.replace('(', '').replace(')', '')}
        main_description_el = header_el.getnext().getnext()
        retval['description_text'] = main_description_el.text if main_description_el.text is not None\
            else str(main_description_el.xpath('string()')).strip()
        params_el = ContentParser._get_params_el(main_description_el)
        retval['params'] = ContentParser._read_function_params(params_el)
        return retval

    @staticmethod
    def _get_params_el(el):
        if el.xpath('string()').strip() == 'PARAMETERS':
            return el.getnext()
        else:
            return ContentParser._get_params_el(el.getnext())

    @staticmethod
    def _read_function_params(table):
        rows = table.xpath('tr')
        l = []
        for r in rows:
            tds = r.xpath('td')
            l.append({
                'name': tds[0].xpath('code')[0].text.replace('[', '').replace(']', ''),
                'types': tds[1].xpath('code')[0].text.split(', '),
                'descr': tds[2].text
            })
        return l

    @staticmethod
    def read_constant_data(header_el):
        retval = {
            'const_name': header_el.text,
            'description_text': header_el.getnext().text
        }
        return retval
