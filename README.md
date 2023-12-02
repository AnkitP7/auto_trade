
# Auto-Trade

Execute options-trade automatically via Kite.

## Installation

### Back-end

#### 1. Create a virtual environment using python.
```bash
   pip install virtualenv
   virtualenv -p <location to python binary> <myenv>
```
#### 2. Activate the environment
```bash
    source <myenv>/bin/activate
```
#### 3. Install the dependencies
```bash
    cd auto_trade/
    pip install -r requirements.txt
```


### Front-end

#### 1. Install the dependencies using npm

```bash
   cd auto_trade/
   npm install
```
## Run Locally
### Back-end

#### 1. Modify .flaskenv and start the development server by running the following commands

```bash
  flask run
```

### Front-end
#### 2. Start development server by running the following command

```bash
  npm start
```


## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`APP_TOKEN` -- API Key required for Kite connect


## API Reference

#### /connect/login/

```http
  GET /connect/login/
```


