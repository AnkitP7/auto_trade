
import requests
import json
from flask import render_template
from kiteconnect import KiteConnect

class RequestHelper:

    KITE_TOKEN_URL = "https://api.kite.trade/session/token"

    def request_for_token(self, data):
        from main import app
        """
        The function `request_for_token` sends a request to obtain an access token and updates the data
        with the response if the status is 200, then returns a session object.
        :return: the result of the `kite_helper._session(data)` function call.
        """
        access_token = requests.post(self.KITE_TOKEN_URL, data={
            'api_key': app['API_KEY'],
            'request_token': data['request_token']
        })
        if access_token.status == 200:
            data.update(json.loads(access_token))
        return data        

class KiteConnectHelper(RequestHelper):

    def __init__(self, api_key) -> None:
        self.api_key = api_key
        self.kite_connect = KiteConnect(api_key)
    
    def _connect(self, ) :
        """
        Redirect the user to the login url obtained
        from kite.login_url(), and receive the request_token
        from the registered redirect url after the login flow.
        Once you have the request_token, obtain the access_token
        as follows.
        """
        #Sample URL https://kite.zerodha.com/connect/login?v=3&api_key=xxx&redirect_params=some%3DX%26more%3DY
        # login_url = self.kite_connect.login_url()
        login_url = f"https://kite.zerodha.com/connect/login?api_key={self.api_key}"
        return render_template('connect.html', context={'url': login_url })
        
    def _session(self, data):
        """
        The function `_session` generates a session using a request token and sets the access token for
        the Kite API.
        
        :param data: The `data` parameter is a dictionary that contains the request token. The request
        token is used to generate a session token, which is then used to authenticate and authorize the
        API requests
        :return: the `kite` object.
        """
        data = self.kite_connect.generate_session(data["request_token"], api_secret=data["api_secret"])
        self.request_for_token(data["request_token"])
        self.kite_connect.set_access_token(data["access_token"])
        return self.kite_connect