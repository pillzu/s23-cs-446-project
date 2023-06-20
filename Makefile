debug:
	@echo "Extracting deps info..."
	@pip3 freeze > requirements.txt
	@echo "Running debugging server listening on local network..."
	@flask run --debug

run-dev:
	@echo "Extracting deps info..."
	@pip3 freeze > requirements.txt
	@echo "Running server listening on local network..."
	@flask run

run-local:
	@echo "Extracting deps info..."
	@pip3 freeze > requirements.txt
	@echo "Running server listening on all IPs..."
	@flask run --host=0.0.0.0

install-deps:
	@echo "Creating virtual env and installing deps..."
	@python3 -m venv .venv
	@. ./.venv/bin/activate
	@echo "Virtual Env Created..."
	@pip3 install -r requirements.txt
