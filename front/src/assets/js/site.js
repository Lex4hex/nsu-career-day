const getNode = id => {
	return document.getElementById(id);
};
const startGettingNodes = () => {};
const openRegistration = () => {
	fetch('http://localhost:8034/openRegistration', {
		method: 'POST',
		mode: 'no-cors', // no-cors, cors, *same-origin
		cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
		credentials: 'same-origin', // include, *same-origin, omit
		headers: {
			'Content-Type': 'application/json'
			// "Content-Type": "application/x-www-form-urlencoded",
		},
		credentials: 'include'
	})
		.then(() => {
			getNode('btn-open').classList.toggle('hidden');
			getNode('btn-next').classList.toggle('hidden');
			// getNode('btn-start').classList.toggle('hidden');
		})
		.then(() => {
			startGettingNodes();
		})
		.catch(alert);
};
const getFirstNodeNumbers = () => {
	fetch('http://localhost:8034/node/FIRST/data', {
		method: 'GET',
		mode: 'no-cors', // no-cors, cors, *same-origin
		cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
		credentials: 'same-origin', // include, *same-origin, omit
		headers: {
			'Content-Type': 'application/json'
			// "Content-Type": "application/x-www-form-urlencoded",
		},
		credentials: 'include'
	})
		.then(result => {
			getNode('btn-open').classList.toggle('hidden');
			getNode('btn-next').classList.toggle('hidden');
			// getNode('btn-start').classList.toggle('hidden');
		})
		.then(() => {
			startGettingNodes();
		})
		.catch(alert);
};
