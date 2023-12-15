// third-party
import { combineReducers } from 'redux';

// project import
import menu from './menu';
import authentication from './authentication';
import configuration from './configuration';

// ==============================|| COMBINE REDUCERS ||============================== //

const reducers = combineReducers({ menu, authentication, configuration });

export default reducers;
