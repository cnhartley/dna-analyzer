<%@page import="com.dnaa.common.dbi.DatabaseInterface"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% session.setAttribute("pageName", "Nucleotide Search"); %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./pagetemplate/headbase.jsp" %>
<link rel="stylesheet" type="text/css"  href="./css/dnaa-forms.css"></link>
<style type="text/css">

form fieldset table {
	width: 100%;
}
form fieldset table thead {
	background-color: #888888;
}
form fieldset table thead tr th {
	font-weight: bold;
}
form fieldset table thead tr th, form fieldset table tbody tr td  {
    color: #000000;
	border-top: 1px solid grey;
    border-bottom: 1px solid grey;
    border-collapse: collapse;
    padding: 5px;
}
form fieldset table tbody tr td:FIRST-CHILD  {
	text-align: center;
	vertical-align: middle;
	margin-left: auto;
	margin-right: auto;
}
form fieldset table tbody tr:nth-child(odd)	{
    background-color: #f1f1f1;
}
form fieldset table tbody tr:nth-child(even) {
    background-color: #ffffff;
}


form fieldset ul li input[type=text] ~ input[type=submit] ~ label {
	padding: 0.25em;
	margin: 0.25em 0em;
    display: inline-block;
    float: left;    
}
form fieldset ul li input[type=submit] ~ label ~ input[type=text] {
    padding: 0.25em;
	margin: 0.25em 0em;
    width: 100%;
    height: inherit;
    display: inline-block;
    float: right;    
}
form fieldset ul li label ~ input[type=text] ~ input[type=submit] {
    padding: 0.25em;
	margin: 0.25em 0em;
    display: inline-block;
    float: right;    
}

</style>
<script type="text/javascript">

$(document).ready(function() {
	$('form').submit(function(event) {
		var protocol = "http://",
		    host = "localhost",
		    port = ":" + 8080,
		    service = "/dnaa/services/search",
		    criteria = $("input[name=criteria]").val(),
		    params = {
		    		'criteria'  : criteria,
		    		'filter'    : null,
		    };
		
		console.log("search for: '" + criteria + "' from: " + protocol + host + port + service);
		$.ajax({
			type:		'GET',
			url:		protocol + host + port + service,
			data:		params,
			dataType:	'json',
			encode:		true,
		})
		.done(function(data) {
			console.log("search returned: " + data);
			processSearchResults(data);
		});
		
		event.preventDefault();
	});
});

function processSearchResults(json) {
	var i = 0,
	    match = {},
	    prop = {},
	    results = "";//"<span>Search results for <em>\"" + json.criteria + "\"</em></span><table>";
	
	if (json.matches) {
		while (i < json.matches.length) {
			match = json.matches[i++];
			results += "<tr><td><input type='checkbox' id='' name='' /></td>";
			results += "<td><a href=\"" + match.url + "\">" + match.name + "</a></td>";
			results += "<td>" + match.size + "</td>";
			results += "<td>" + match.createdby + "</td>"
			         + "<td>" + match.createdon + "</td>";

			results += "</tr>";
		}
	}
	//results += "</table>";
	
	$("#results").html(results);
}

</script>
</head>
<body>
<%@ include file="./pagetemplate/header.jsp" %>
<%@ include file="./pagetemplate/nav.jsp" %>

<section>
	<h1><%=session.getAttribute("pageName") %></h1>
	<p>This allow you to search for a specific nucleotide sequence (RNA or DNA) from our database.</p>
	<form method="get" action="./services/search">
		<fieldset>
			<ul>
				<li>
					<label for="criteria">Search for:</label>
					<input type="text" id="criteria" name="criteria" value="" required="required" />
					<input type="submit" id="submitBtn" name="submitBtn" value="SEARCH..." />
				</li>
			</ul>
			<hr class="clear" />
			<table>
				<thead>
					<tr>
						<th>Compare</th>
						<th>Name / Organism</th>
						<th>Size</th>
						<th>Created By</th>
						<th>Created On</th>
					</tr>
				</thead>
				<tbody id="results" name="results">
					<tr><td colspan="5"><em>[no search results]</em></td></tr>
				</tbody>
			</table>
		</fieldset>
	</form>
	
</section>

<%@ include file="./pagetemplate/footer.jsp" %>
</body>
</html>