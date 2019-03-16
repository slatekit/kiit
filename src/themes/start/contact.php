<?php if ( !isset( $_SESSION ) ) session_start();

if ( !$_POST ) exit;

if ( !defined( "PHP_EOL" ) ) define( "PHP_EOL", "\r\n" );

///////////////////////////////////////////////////////////////////////////

// Simple Configuration Options

// Enter the email address that you want to emails to be sent to.
// Example $address = "joe.doe@yourdomain.com";

$address = "hi@yoarts.com";

// END OF Simple Configuration Options

///////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////
//
// Do not edit the following lines
//
///////////////////////////////////////////////////////////////////////////

$postValues = array();
foreach ( $_POST as $name => $value ) {
	$postValues[$name] = trim( $value );
}
extract( $postValues );


// Important Variables
$posted_verify = isset( $postValues['verify'] ) ? md5( $postValues['verify'] ) : '';
$session_verify = !empty($_SESSION['yoarts']['ajax-extended-form']['verify']) ? $_SESSION['yoarts']['ajax-extended-form']['verify'] : '';

$error = '';

///////////////////////////////////////////////////////////////////////////
//
// Begin verification process
//
// You may add or edit lines in here.
//
// To make a field not required, simply delete the entire if statement for that field.
//
///////////////////////////////////////////////////////////////////////////


////////////////////////
// Subject field is required
if ( empty( $subject ) ) {
	$error .= 'Your subject is required.';
}
////////////////////////


////////////////////////
// Name field is required
if ( empty( $name ) ) {
	$error .= 'Your name is required.';
}
////////////////////////


////////////////////////
// Email field is required
if ( empty( $email ) ) {
	$error .= 'Your e-mail address is required.';
} elseif ( !isEmail( $email ) ) {
	$error .= 'You have entered an invalid e-mail address.';
}
////////////////////////


////////////////////////
// Comments field is required
if ( empty( $message ) ) {
	$error .= 'You must enter a message to send.';
}
////////////////////////


////////////////////////
// Verification code is required
if ( $session_verify != $posted_verify ) {
	$error .= 'The verification code you entered is incorrect.';
}
////////////////////////

if ( !empty($error) ) {
	echo '<div class="alert alert-danger">Oh snap! ' . $error . '</div>';

	// Important to have return false in here.
	return false;

}

// Advanced Configuration Option.
// i.e. The standard subject will appear as, "You've been contacted by John Doe."

$e_subject = "You've been contacted by: " . $name;

// Advanced Configuration Option.
// You can change this if you feel that you need to.
// Developers, you may wish to add more fields to the form, in which case you must be sure to add them here.

$msg  = "You have been contacted by $name with regards to $subject, they passed verification and their message is as follows." . PHP_EOL . PHP_EOL;
$msg .= $message . PHP_EOL . PHP_EOL;
$msg .= "You can contact $name via email, $email." . PHP_EOL . PHP_EOL;
$msg .= "-------------------------------------------------------------------------------------------" . PHP_EOL;
$msg .= "This message was sent to you via the Contact Form";
$msg = wordwrap( $msg, 70 );

$headers  = "From: $email" . PHP_EOL;
$headers .= "Reply-To: $email" . PHP_EOL;
$headers .= "MIME-Version: 1.0" . PHP_EOL;
$headers .= "Content-type: text/plain; charset=utf-8" . PHP_EOL;
$headers .= "Content-Transfer-Encoding: quoted-printable" . PHP_EOL;

if ( mail( $address, $e_subject, $msg, $headers ) ) {

	echo "<div class='alert alert-success'>Well done! Your message has been submitted to us.</div>";

	// Important to have return false in here.
	return false;

}


///////////////////////////////////////////////////////////////////////////
//
// Do not edit below this line
//
///////////////////////////////////////////////////////////////////////////
echo '<div class="alert alert-danger">Error! Please confirm PHP mail() is enabled.</div>';
return false;

function isEmail( $email ) { // Email address verification, do not edit.

	return preg_match( "/^[-_.[:alnum:]]+@((([[:alnum:]]|[[:alnum:]][[:alnum:]-]*[[:alnum:]])\.)+(ad|ae|aero|af|ag|ai|al|am|an|ao|aq|ar|arpa|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|biz|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|com|coop|cr|cs|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|edu|ee|eg|eh|er|es|et|eu|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gh|gi|gl|gm|gn|gov|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|in|info|int|io|iq|ir|is|it|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mil|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|museum|mv|mw|mx|my|mz|na|name|nc|ne|net|nf|ng|ni|nl|no|np|nr|nt|nu|nz|om|org|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|pro|ps|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|um|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw)$|(([0-9][0-9]?|[0-1][0-9][0-9]|[2][0-4][0-9]|[2][5][0-5])\.){3}([0-9][0-9]?|[0-1][0-9][0-9]|[2][0-4][0-9]|[2][5][0-5]))$/i", $email );

}
?>
