const DATA_CONFIG = {
    configuration: {
        columns: [
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
                render: (row) => row.stop_loss_type ? "Percentage" : "Flat"
            },
            {
                id: 'stop_loss_value',
                label: 'Stop Loss Value',
            },
            // {
            //     id: 'created_datetime',
            //     label: 'Created At'
            // }
        ]
    }
}

export { DATA_CONFIG }