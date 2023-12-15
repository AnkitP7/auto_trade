import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
// material-ui
import { Grid, Stack, Typography, InputLabel, OutlinedInput, FormControlLabel, Switch, Button, Select, MenuItem, SwipeableDrawer, Card, CircularProgress, LinearProgress } from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { TimePicker } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import AnimateButton from 'components/@extended/AnimateButton';
import { Formik } from 'formik';
// project import
import DataTable from 'components/dataTable';
import { drawerAction } from 'store/reducers/configuration';
import { PlusOutlined } from '@ant-design/icons';
import { getConfiguration, createConfiguration } from 'services/configuration';
import dayjs from 'dayjs';
var customParseFormat = require('dayjs/plugin/customParseFormat')
dayjs.extend(customParseFormat)

const Configuration = () => {

  const dispatch = useDispatch();
  const state = useSelector((state) => state.configuration.list);
  const columns = [
    {
      id: 'tag',
      label: 'Tag',
    },
    {
      id: 'index_name',
      label: 'Index',
    },
    {
      id: 'days_of_week',
      label: 'Days of Week',
    },
    {
      id: 'start_time',
      label: 'Start Time',
    },
    {
      id: 'end_time',
      label: 'End Time',
    },
    {
      id: 'quantity',
      label: 'Quantity',
    },
    {
      id: 'entry_criteria',
      label: 'Entry criteria',
    },
    {
      id: 'entry_criteria_value',
      label: 'Entry Criteria Value',
    },
    {
      id: 'stop_loss_type',
      label: 'Stop Loss Type',
    },
    {
      id: 'stop_loss_value',
      label: 'Stop Loss Value',
    },
    {
      id: 'is_enabled',
      label: 'Active'
    }
  ]

  let dataTableProps = {
    columns,
    rows: state.data,
  }

  useEffect(() => {
    dispatch(getConfiguration());
  }, [])

  return (
    <Grid container rowSpacing={2} columnSpacing={2} justifyContent={'flex-end'}>
      <Grid item>
        <Button variant="contained" startIcon={<PlusOutlined />} onClick={() => { dispatch(drawerAction(true)) }}>
          Create
        </Button>
      </Grid>
      <Grid item lg={24} md={12} xs={6}>
        <DataTable {...dataTableProps} />
      </Grid>
      <Grid item lg={24} md={12} xs={6}>
        <ConfigurationForm />
      </Grid>
    </Grid>
  )
}

const ConfigurationForm = () => {

  const dispatch = useDispatch();
  const state = useSelector((state) => state.configuration.create);

  return (
    <SwipeableDrawer
      sx={{
        width: '20%',
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          marginTop: '2.5%',
          width: '20%',
          boxSizing: 'border-box',
        },
      }}
      anchor={"right"}
      open={state.isVisible}
      hideBackdrop={true}
      onClose={() => dispatch(drawerAction(false))}
      onOpen={() => dispatch(drawerAction(true))}
    >
      <Card style={{ margin: 30, border: 'none', boxShadow: "none" }}>
        {state.isLoading ? <CircularProgress /> : null}
        <Formik
          initialValues={{
            tag: '',
            indexName: '',
            daysOfWeek: 1,
            startTime: null,
            formattedStartTime: null,
            endTime: null,
            formattedEndTime: null,
            quantity: null,
            entryCriteria: null,
            entryCriteriaValue: null,
            stopLossType: null,
            stopLossValue: null,
            isEnabled: false,
          }}
          onSubmit={async (values, { setErrors, setStatus, setSubmitting }) => {
            try {
              dispatch(createConfiguration(values))
              setStatus({ success: false });
              setSubmitting(false);
            } catch (err) {
              setStatus({ success: false });
              setErrors({ submit: err.message });
              setSubmitting(false);
            }
          }}
        >
          {({ errors, handleBlur, handleChange, handleSubmit, isSubmitting, touched, values, setFieldValue }) => (
            <form noValidate onSubmit={handleSubmit}>
              <Grid container lg={24} spacing={1} style={{ overflowY: 'auto' }}>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Tag">Tag</InputLabel>
                    <OutlinedInput
                      id="tag"
                      value={values.tag}
                      name="tag"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter a tag to identify your configuration"
                      fullWidth
                      error={Boolean(touched.tag && errors.tag)}
                    />
                    {touched.tag && errors.tag && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.tag}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Index Name">Index Name</InputLabel>
                    <OutlinedInput
                      id="indexName"
                      value={values.indexName}
                      name="indexName"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter valid index name"
                      fullWidth
                      error={Boolean(touched.indexName && errors.indexName)}
                    />
                    {touched.indexName && errors.indexName && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.indexName}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="daysOfWeek">Days of Week</InputLabel>
                    <Select
                      name="daysOfWeek"
                      value={values.daysOfWeek}
                      label="Age"
                      onChange={handleChange}
                    >
                      {[1, 2, 3, 4, 5, 6,].map((day, index) => <MenuItem key={day} value={day}>{day}</MenuItem>)}
                    </Select>
                    {touched.daysOfWeek && errors.daysOfWeek && (
                      <FormHelperText error id="standard-weight-helper-text-daysOfWeek">
                        {errors.daysOfWeek}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Start Time">Start Time</InputLabel>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                      <TimePicker
                        label="Start Time"
                        id="startTime"
                        ampm={false}
                        value={values.startTime}
                        onChange={(value) => { setFieldValue("startTime", value, true); setFieldValue("formattedStartTime", dayjs(value).format('HH:MM:ss')) }}
                      />
                    </LocalizationProvider>
                    {touched.startTime && errors.startTime && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.startTime}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="End Time">End Time</InputLabel>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                      <TimePicker
                        label="End Time"
                        id="endTime"
                        ampm={false}
                        value={values.endTime}
                        onChange={(value) => { setFieldValue("endTime", value, true); setFieldValue("formattedEndTime", dayjs(value).format('HH:MM:ss')) }}
                      />
                    </LocalizationProvider>
                    {touched.endTime && errors.endTime && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.endTime}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Quantity">Quantity</InputLabel>
                    <OutlinedInput
                      id="quantity"
                      value={values.quantity}
                      name="quantity"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter valid quantity"
                      fullWidth
                      error={Boolean(touched.quantity && errors.quantity)}
                    />
                    {touched.quantity && errors.quantity && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.quantity}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Entry Criteria">Entry Criteria</InputLabel>
                    <OutlinedInput
                      id="entryCriteria"
                      value={values.entryCriteria}
                      name="entryCriteria"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter entry criteria"
                      fullWidth
                      error={Boolean(touched.entryCriteria && errors.entryCriteria)}
                    />
                    {touched.entryCriteria && errors.entryCriteria && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.entryCriteria}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Entry Criteria value">Entry Criteria Value</InputLabel>
                    <OutlinedInput
                      id="entryCriteriaValue"
                      value={values.entryCriteriaValue}
                      name="entryCriteriaValue"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter enter entry criteria value"
                      fullWidth
                      error={Boolean(touched.entryCriteriaValue && errors.entryCriteriaValue)}
                    />
                    {touched.entryCriteriaValue && errors.entryCriteriaValue && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.entryCriteriaValue}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Stop Loss Type">Stop Loss type</InputLabel>
                    <Select
                      name="stopLossType"
                      value={values.stopLossType}
                      label="Age"
                      onChange={handleChange}
                    >
                      <MenuItem value={0}>Fixed</MenuItem>
                      <MenuItem value={1}>Percentage</MenuItem>
                    </Select>
                    {touched.stopLossType && errors.stopLossType && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.stopLossType}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Stop Loss value">Stop Loss Value</InputLabel>
                    <OutlinedInput
                      id="stopLossValue"
                      value={values.stopLossValue}
                      name="stopLossValue"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter enter stop los value"
                      fullWidth
                      error={Boolean(touched.stopLossValue && errors.stopLossValue)}
                    />
                    {touched.stopLossValue && errors.stopLossValue && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.stopLossValue}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} sx={{ mt: -1 }} lg={24}>
                  <Stack direction="row">
                    <FormControlLabel
                      value={values.isEnabled}
                      control={
                        <Switch defaultChecked size="large" handleChange={handleChange} />
                      }
                      label={<Typography variant="h6">Enabled</Typography>}
                    />
                  </Stack>
                </Grid>
                {errors.submit && (
                  <Grid item xs={12}>
                    <FormHelperText error>{errors.submit}</FormHelperText>
                  </Grid>
                )}
                <Grid item xs={12} lg={12}>
                  <AnimateButton>
                    <Button disableElevation disabled={isSubmitting} fullWidth size="large" type="submit" variant="contained" color="primary">
                      Create
                    </Button>
                  </AnimateButton>
                </Grid>
              </Grid>
            </form>
          )}
        </Formik>
      </Card>
    </SwipeableDrawer>
  )
}

export default Configuration;
