$(document).ready(function() {
	console.log('document ready');
	var elements = ['lights', 'temperature', 'curtains', 'photo-frame'];
	var elementsButtons = ['lights-button', 'temperature-button', 'curtains-button', 'photo-frame-button'];
	
	function hideAll() {
		for (var i=0; i<elements.length; i++) {
			$('#' + elements[i]).addClass('not-visible');
		}
	}
	
	function showById(id) {
		hideAll();
		$('#' + id).removeClass('not-visible');
	}
	
	function addButtonsCallbacks() {
		for (var i=0; i<elementsButtons.length; i++) {
			$('#' + elementsButtons[i]).click(function() {
				var idName = $(this).attr('id');
				var elementIdName = idName.replace('-button', '');
				showById(elementIdName);
			});
		}
	}
	
	function addTemperatureCallback() {
		$('#change-temperature-button').click(function() {
			$('#show-temperature').addClass('not-visible');
			$('#edit-temperature').removeClass('not-visible');
		});
	}
	
	addButtonsCallbacks();
	addTemperatureCallback();
	
	
	var toggles = ['light1', 'light2', 'light3', 'curtains1'];
	
	function initTogglesCallbacks(name) {
		$('#' + name).change(function() {
			console.log($(this).attr('id'));
			console.log($(this).prop('checked'));

			var itemName =  $(this).attr('id');
			var json = {
				"name": itemName,
				"on": $(this).prop('checked')
			};
			
			$.post('sensors/' + itemName + '.json' , JSON.stringify(json)).done(function(data) {
				console.log("success");
				console.log(data);
			});
		});
	}
	
	function initToggles() {
		for (var i=0; i<toggles.length; i++) {
			// Get the state of the sensors
			$.get("sensors/" + toggles[i] + ".json", function(data) {
				if (data.on) {
					$('#' + data.name).bootstrapToggle('on');
				}
				else {
					$('#' + data.name).bootstrapToggle('off');
				}
				initTogglesCallbacks(data.name);
			});
		}
	}
	initToggles();
	
	function getTemperature() {
		$.get('sensors/temperature1.json', function(data) {
			console.log("value");
			console.log(data);
			$('#' + data.name).html(data.value);
			$('#input-' + data.name).val(data.value);
		});
	}
	
	function initTemperature() {
		getTemperature();
		
		$('#save-button-temperature1').click(function() {
			var value = $('#input-temperature1').val();
			var json = {"name": "temperature1", "value":value};
			$.post('sensors/temperature1.json' , JSON.stringify(json)).done(function(data) {
				console.log("success");
				console.log(data);
				
				// Switch back to temperature text and update value			
				getTemperature();
				$('#edit-temperature').addClass('not-visible');
				$('#show-temperature').removeClass('not-visible');
			});
		});
	}
	initTemperature();
	
	function initImage() {
		$('#image-form').submit(function(e) {
			// Prevent redirect
			e.preventDefault();
			var form = $(this);
			var formData = new FormData(form[0]);
			// Overwrite image on server
			$.ajax({
				type: 'PUT',
				url: 'imgs/image.jpg',
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					// Form image reload
					$('#image').removeAttr('src');
					setTimeout(function() {
						$("#image").attr('src', '/imgs/image.jpg');
					}, 1000);
				}
			})
		});
	}
	initImage();
	
	function initDeleteImage() {
		$('#image-delete').click(function() {
			$.ajax({
				type: 'DELETE',
				url: 'imgs/image.jpg',
				success: function(data) {
					$('#image').removeAttr('src');
				}
			})
		});
	}
	initDeleteImage();
});