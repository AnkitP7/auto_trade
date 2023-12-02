from kiteconnect import KiteConnect
from flask import render_template

class KiteConnectHelper:

    def __init__(self, api_key) -> None:
        self.api_key = api_key
        self.kite_connect = KiteConnect(api_key)
    
    def _connect(self, ):
        """
        Redirect the user to the login url obtained
        from kite.login_url(), and receive the request_token
        from the registered redirect url after the login flow.
        Once you have the request_token, obtain the access_token
        as follows.
        """
        #Sample URL https://kite.zerodha.com/connect/login?v=3&api_key=xxx&redirect_params=some%3DX%26more%3DY
        # login_url = self.kite_connect.login_url()
        login_url = f"https://kite.zerodha.com/connect/login?v={self.api_key}"
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
        self.kite_connect.set_access_token(data["access_token"])
        return self.kite_connect