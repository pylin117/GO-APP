import sys
import json
import requests
from pyquery import PyQuery as pq
import datetime as dt

url_base = 'https://www.instagram.com/explore/tags/'
date = []


headers = {
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36',
    #    'cookie': 'mid=XmndKwALAAGzzT_tlQNMqOzF1hlj; csrftoken=lEaA4MDdA7Xb0YJriaJShvIuhRxoUc8p; shbid=10695; shbts=1583996243.7516522; ds_user_id=1304979710; sessionid=1304979710%3AEnQzbsoxLYEi7H%3A11; rur=FTW'
}

def get_html(url):
    try:
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            return response.text
        else:
            print('請求網頁源代碼錯誤, 錯誤狀態碼：', response.status_code)
            check = False
            return None
    except Exception as e:
        print("123")
        check = False
        return None


def get_urls(html):
    urls = []

    doc = pq(html)
    items = doc('script[type="text/javascript"]').items()
    for item in items:
        if item.text().strip().startswith('window._sharedData'):
            js_data = json.loads(item.text()[21:-1], encoding='utf-8')
            edges = js_data["entry_data"]["TagPage"][0]["graphql"]["hashtag"]["edge_hashtag_to_media"]["edges"]
            for edge in edges:
                if edge['node']['display_url']:
                    display_url = edge['node']['display_url']
                    display_date = edge['node']['taken_at_timestamp']
                    urls.append(display_url)
                    date.append(dt.datetime.fromtimestamp(display_date).strftime('%Y-%m-%d %H:%M:%S'))
    return urls

def main(user):

    url = url_base + user + '/'
    html = get_html(url)

    if html == None:
        return 0
    #print(html)
    urls = get_urls(html)

    return len(date);

if __name__ == '__main__':
    user_name = sys.argv[1]
    main(user_name)