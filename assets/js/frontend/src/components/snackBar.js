import PropTypes from 'prop-types';
import { Snackbar } from '@mui/base';
import { Alert } from '@mui/material';

const AppSnackBar = (props) => {
    if (props.open) {
        return (
            <Snackbar {...props}>
                <Alert severity="info">
                    {props.message}
                </Alert>
            </Snackbar >
        )
    }
    return null;
}

AppSnackBar.propTypes = {
    open: PropTypes.bool,
    autoHideDuration: PropTypes.number,
    onClose: PropTypes.func,
    TransitionComponent: PropTypes.object,
    message: PropTypes.string,
    key: PropTypes.object,
}

AppSnackBar.defaultProps = {
    open: false,
    autoHideDuration: 3000,
    onClose: () => { },
    message: "",
    key: "app-snackbar",
}

export default AppSnackBar;