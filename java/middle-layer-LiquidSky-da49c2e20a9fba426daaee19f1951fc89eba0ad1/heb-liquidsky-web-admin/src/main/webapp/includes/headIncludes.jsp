
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.8.0/jquery.modal.css">
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jquery-mobile/1.4.5/jquery.mobile.icons.min.css" />
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jquery-mobile/1.4.5/jquery.mobile.min.css">

<script src="//ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
<script>
	$(document).on("mobileinit", function(){
		// jquery mobile likes to do ajax loads of pages, but that totally messes up our other JS, so this disables it.
		console.log("fired");
		$.mobile.ajaxEnabled = false;
	});
</script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery-mobile/1.4.5/jquery.mobile.min.js"></script>
