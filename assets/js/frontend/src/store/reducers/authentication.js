// types
import { createSlice } from '@reduxjs/toolkit';
import { login } from '../../services/authentication';
import LocalStorage from '../../services/localStorage';

const ls = new LocalStorage();
// initial state
const initialState = {
    data: {},
    isLoading: false,
    error: {},
};

const authentication = createSlice({
    name: 'authentication',
    initialState,
    reducers: {
        closeSnackBar: (state, action) => {
            state.error.open = action.payload.status;
        },
    },
    extraReducers: (builder) => {
        builder.addCase(login.pending, (state, action) => {
            state.isLoading = true;
        })
        builder.addCase(login.fulfilled, (state, action) => {
            state.isLoading = false;
            if (action.payload.status){
                state.data = action.payload.data;
                localStorage.setItem('authorization', JSON.stringify(action.payload.data))
            }
            else
                state.error = { ...action.payload.error, open: true, message: action.payload.error.message, }
        })
        builder.addCase(login.rejected, (state, action) => {
            state.error = action.payload;
        })
    }
});

export default authentication.reducer;

export const { closeSnackBar, routeToDashboard } = authentication.actions;