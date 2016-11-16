<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<head>

	<!-- Page configuration -->
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title>Blockchain2graph console</title>
	<meta name="description" content="Blockchain2graph console">

	<!--Let browser know this website is optimized for mobile-->
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<!-- Fonts -->
	<link href='http://fonts.googleapis.com/css?family=Oswald:400,300,700' rel='stylesheet' type='text/css'>

	<!-- CSS lib -->
	<link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/bootstrap/css/bootstrap.min.css'>

	<!-- Javascript lib -->
	<script src="${pageContext.request.contextPath}/webjars/jquery/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/webjars/bootstrap/js/bootstrap.min.js"></script>

	<!-- Custom CSS & Javascript -->
	<link rel='stylesheet' href='${pageContext.request.contextPath}/css/custom.css'>
	<script>
		var url = "${url}/${pageContext.request.contextPath}";
	</script>
	<script src="${pageContext.request.contextPath}/js/console.js"></script>

</head>
<body>

<div class="container">

	<div id='root'></div>
	<!-- ----------------------------------------------------------------------------------------------------------- -->
	<!-- Header (Title / number of blocks integrated / total number of blocks -->
	<div class="row header">
		<div class="col-sm-8"><h1>Blockchain2graph console</h1></div>
		<div class="col-sm-4">
			<h2>
				<div id='importedBlockCount' style="display: inline"></div>
				<div style="display: inline">/</div>
				<div id='totalBlockCount' style="display: inline"></div>
			</h2>
		</div>
	</div>
	<!-- Last error message -->
	<div class="row">
		<div class="col-sm-1">&nbsp;</div>
		<div class="col-sm-10 errorMessage" id="lastErrorMessage">No errors.</div>
		<div class="col-sm-1">&nbsp;</div>
	</div>
	<!-- Log display -->
	<div class="row logs">
		<div class="col-sm-12">
			<div id="logs"></div>
		</div>
	</div>
	<!-- Footer -->
	<div class="row footer">
		<div class="col-sm-12">
			<p><a href="https://straumat.github.io/blockchain2graph/" target="blockchain2graph">blockchain2graph</a></p>
		</div>
	</div>
	<!-- ----------------------------------------------------------------------------------------------------------- -->

</div>

</body>
</html>