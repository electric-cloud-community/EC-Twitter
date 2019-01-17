my %UpdateStatus = (
    label       => "Twitter - Update Status",
    procedure   => "UpdateStatus",
    description => "Updates the status on twitter",
    category    => "Notification"
);

$batch->deleteProperty("/server/ec_customEditors/pickerStep/EC-Twitter - UpdateStatus");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/Twitter - Update Status");

@::createStepPickerSteps = (\%UpdateStatus);
