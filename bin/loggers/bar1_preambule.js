function addInterProduct(id, qtt, price)
{
	var objPrice = document.getElementsByName("provimoney_" + id),
		objQtt = document.getElementsByName("proviqty_" + id);
	if ((objPrice.length != 1) || (objQtt.length != 1))
		throw new Error('Problem with id ' + id);
	if (objPrice[0].value == "")
	{
		objPrice[0].value = price;
		if (id == 337)
			objQtt[0].value = price;
		else objQtt[0].value = qtt;
	} else {
		objPrice[0].value = parseFloat(objPrice[0].value) + parseFloat(price);
		if (id == 337)
			objQtt[0].value = parseFloat(objQtt[0].value) + parseFloat(price);
		else objQtt[0].value = parseFloat(objQtt[0].value) + parseFloat(qtt);
	}
}
