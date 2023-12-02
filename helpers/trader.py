import pandas as pd
import numpy as np
from kiteconnect import KiteConnect
import datetime
import time
import urllib3
import sys
import uuid

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# stoploss should not go up
# all days: friday and monday
# both orders are independent
# drawdown, win loss on days, total pnl
# 10:30 and 12:30
# redislabs

# # Products
# PRODUCT_MIS = "MIS"
# PRODUCT_CNC = "CNC"
# PRODUCT_NRML = "NRML"
# PRODUCT_CO = "CO"

# # Order types
# ORDER_TYPE_MARKET = "MARKET"
# ORDER_TYPE_LIMIT = "LIMIT"
# ORDER_TYPE_SLM = "SL-M"
# ORDER_TYPE_SL = "SL"

# # Varities
# VARIETY_REGULAR = "regular"
# VARIETY_CO = "co"
# VARIETY_AMO = "amo"

# # Transaction type
# TRANSACTION_TYPE_BUY = "BUY"
# TRANSACTION_TYPE_SELL = "SELL"

# # Validity
# VALIDITY_DAY = "DAY"
# VALIDITY_IOC = "IOC"

# # Exchanges
# EXCHANGE_NSE = "NSE"
# EXCHANGE_BSE = "BSE"
# EXCHANGE_NFO = "NFO"
# EXCHANGE_CDS = "CDS"
# EXCHANGE_BFO = "BFO"
# EXCHANGE_MCX = "MCX"
api_key = 'YOUR_API_KEY'
api_secret = 'YOUR_API_SECRET'

kite = KiteConnect(api_key=api_key)

logging.basicConfig(level=logging.INFO)

config = read_config()


def get_stack_trace():
    exc_type, exc_value, exc_tb = sys.exc_info()
    return traceback.format_exception(exc_type, exc_value, exc_tb)


def check_market_open():
    now = datetime.datetime.now()
    if now.hour >= 9 and now.hour < 15:
        return True
    else:
        return False


def check_market_close():
    now = datetime.datetime.now()
    if now.hour > 15:
        return False
    else if now.hour == 15 and now.minute > 30:
        return False
    else
    return true


# discuss how does the login work with Amit/Santosh
def login():
    url = kite.login_url()
    print(url)
    request_token = input("Enter the request token")
    request_response = kite.request_access_token(request_token, api_secret)
    kite.set_access_token(request_response['access_token'])


# stoploss, tickers to trade, days to trade, start_time, end_time,
def read_strat_config():
    return None


# offset of the options, expiry where?  what about holidays, closest expiry how
def read_config():
    return None


def calculate_atm_strike_price_for_call(ticker):
    spot_price = get_ltp(ticker)
    offset = config["offset"][ticker]
    atm_call_strike = round(spot_price / offset) * offset
    return atm_call_strike


def calculate_atm_strike_for_put(ticker):
    spot_price = get_ltp(ticker)
    offset = config["offset"][ticker]
    atm_put_strike = calculate_atm_strike_price_for_call(ticker) - offset
    return atm_put_strike


def calculate_option_strike_for_call(atm_price, offset, direction):
    return None


def calculate_option_strike_for_put(atm_price, offset, direction):
    return calculate_atm_strike_price_for_call


def get_ltp(ticker):
    try:
        ltp_dict = kite.ltp(ticker)
        return ltp_dict[ticker]['last_price']
    except Exception as e:
        :
    trace = get_stack_trace()
    print("Failed to fetch ltp: {} and trace is {}".format(e, trace))
    return None


def calculate_symbol_to_be_traded(ticker, expiry, type, strike_price):
    return ticker + expiry.strftime("%b").upper() + strike_price + type


def get_instruments_list_from_kite():
    kite_df = pd.read_csv(kite.instruments())
    return kite_df


def filter_ticker_from_data(kite_df, ticker, symbol_to_filter_from_df):
    return kite_df[kite_df[symbol_to_filter_from_df] == ticker]


symbol_to_filter = "tradingsymbol"


def get_closest_expiry(ticker):
    try:
        kite_df = get_instruments_list_from_kite()
        ticker_df = filter_ticker_from_data(kite_df, ticker, symbol_to_filter)
        expiry_dates = set(option['expiry'] for option in nifty_options_instruments)
        current_date = datetime.now()
        closest_expiry = min(expiry_dates, key=lambda x: abs(datetime.strptime(x, "%Y-%m-%d") - current_date))
        return closet_expiry
    except:
        trace = get_stack_trace()
        print("Failed to fetch closet expiry: {} and trace is {}".format(e, trace))
        return None


# https://github.com/aeron7/Zerodha_Live_Automate_Trading-_using_AI_ML_on_Indian_stock_market/blob/master/Getting%20Started%20with%20Zerodha.ipynb
# https://www.youtube.com/watch?v=9vzd289Eedk
# https://kite.trade/docs/connect/v3/user/#login-flow

def place_market_order(ticker, quantity, transaction_type, transaction_id):
    try:
        transaction_type = kite.TRANSACTION_TYPE_BUY if transaction_type == "BUY" else kite.TRANSACTION_TYPE_SELL
        kite_order_id = kite.place_order(variety="regular", tradingsymbol=ticker, quantity=quantity,
                                         exchange=kite.EXCHANGE_NSE, order_type="MARKET",
                                         transaction_type=transaction_type, product='MIS', tag=transaction_id)[
            'order_id']
        logging.info(
            "{} market order placed {} quantity {} ID is: {} and kite_order_id is {}".format(transaction_type, ticker,
                                                                                             quantity, transaction_id,
                                                                                             kite_order_id))
        return kite_order_id
    except Exception as e:
        trace = get_stack_trace()
        logging.info("Failed to place market order for ticker {} due to: {} and trace is {}".format(ticker, e, trace))
        return None


def place_limit_order(ticker, price, quantity, transaction_type, transaction_id):
    try:
        transaction_type = kite.TRANSACTION_TYPE_BUY if transaction_type == "BUY" else kite.TRANSACTION_TYPE_SELL
        kite_order_id = kite.place_order(variety="regular", tradingsymbol=ticker, quantity=quantity,
                                         exchange=kite.EXCHANGE_NSE, order_type="LIMIT", price=price,
                                         transaction_type=transaction_type, product='MIS', tag=transaction_id)[
            'order_id']
        logging.info(
            "{} limit order placed {} quantity {} ID is: {} and kite_order_id is {}".format(transaction_type, ticker,
                                                                                            quantity, transaction_id,
                                                                                            kite_order_id))
        return kite_order_id
    except Exception as e:
        trace = get_stack_trace()
        logging.info("Failed to place limit order for ticker {} due to: {} and trace is {}".format(ticker, e, trace))
        return None


def cancel_order(order_id, transaction_id):
    try:
        kite_order_id = kite.cancel_order("regular", order_id=order_id, parent_order_id=None, tag=transaction_id)[
            'order_id']
        logging.info("Cancel order placed for ID {}, newly order_id is {} and kite_order_id is {}".format(order_id,
                                                                                                          transaction_id,
                                                                                                          kite_order_id))
        return kite_order_id
    except Exception as e:
        trace = get_stack_trace()
        logging.info("Failed to place cancel order for order {} due to: {} and trace is {}".format(order_id, e, trace))
        return None


def modify_order_to_market(order_id, transaction_id):
    try:
        kite_order_id = \
        kite.modify_order(variety="regular", order_id=order_id, tag=transaction_id, order_type="MARKET")['order_id']
        logging.info(
            "Modify order placed for ID {}, newly internal_order_id is {} and kite_order_id is {}".format(order_id,
                                                                                                          transaction_id,
                                                                                                          kite_order_id))
        return kite_order_id
    except Exception as e:
        trace = get_stack_trace()
        logging.info("Failed to place modify order for order {} due to: {} and trace is {}".format(order_id, e, trace))
        return None


def exit_from_position_given_an_order_id(order_id, transaction_id):
    try:
        kite_order_id = kite.exit_order(variety="regular", order_id=order_id, tag=transaction_id)
        logging.info(
            "Exit order placed {} for ID {}, newly order_id is {}".format(order_id, transaction_id, kite_order_id))
        return kite_order_id
    except:
        trace = get_stack_trace()
        logging.info("Failed to exit from position for order {} due to: {} and trace is {}".format(order_id, e, trace))
        return None


def exit_all_positions():
    try:
        positions = kite.positions()['day']
        for position in positions:
            ticker = position['tradingsymbol']
            exchange = position['exchange']
            quantity = abs(position['quantity'])
            kite_order_id = exit_from_position(order_id, uuid.uuid4())
            logging.info(
                "Exit order placed for {} units of {} with kite_order_id {}".format(quantity, ticker, kite_order_id))
    except Exception as e:
        trace = get_stack_trace()
        logging.info("Failed to exit positions: {} and trace is {}".format(e, trace))


def calculate_position_pnl():
    try:
        positions = kite.positions()['net']
        pnl = 0.0
        for position in positions:
            average_price = position['average_price']
            quantity = position['quantity']
            instrument_token = position['instrument_token']
            ticker = kite.ltp(instrument_token)
            current_price = ticker[str(instrument_token)]['last_price']
            if position['quantity'] > 0:
                pnl += (current_price - average_price) * quantity
            else:
                pnl += (average_price - current_price) * abs(quantity)
        logging.info("PNL at time {} is {}".format(datetime.datetime.now(), pnl))
        return pnl
    except Exception as e:
        trace = get_stack_trace()
        logging.info("Failed to calculate day's PnL due to: {} and trace is {}".format(e, trace))
        return None


def calculate_pnl():
    orders = []
    try:
        orders = kite.orders()
    except Exception as e:
        trace = get_stack_trace()
        logging.info("Failed to calculate day's PnL due to: {} and trace is {}".format(e, trace))
        return None
    pnl = 0.0
    for order in orders:
        if order['status'] == 'COMPLETE' and order['transaction_type'] == 'BUY':
            if 'average_price' in order and 'price' in order:
                pnl += (order['price'] - order['average_price']) * order['quantity']
        elif order['status'] == 'COMPLETE' and order['transaction_type'] == 'SELL':
            if 'average_price' in order and 'price' in order:
                pnl += (order['average_price'] - order['price']) * order['quantity']
    logging.info("PNL for date {} is {}".format(datetime.datetime.now().date(), pnl))
    return pnl


def fetch_order_details():
    return None


def get_profile():
    return kite.profile()


if __name__ == '__main__':
    read_config()
    read_strat_config()
    login()
    get_ltp()
    calculate_atm()
    get_closet_expiry()
    place_market_order()
    place_limit_order()
    exit_all_positions()
    calculate_pnl()
    publish_to_database()
    send_email()



