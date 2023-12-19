// third-party
import { combineReducers } from 'redux';

// project import
import menu from './menu';
import authentication from './authentication';
import configuration from './configuration';
import settings from './settings';

// ==============================|| COMBINE REDUCERS ||============================== //

const reducers = combineReducers({ menu, authentication, configuration, settings });

export default reducers;
