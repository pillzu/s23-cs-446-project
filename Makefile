run:
	@. ./.venv/bin/activate
	@echo "Running server listening on local network..."
	@python3 app.py

install-deps:
	@echo "Creating virtual env and installing deps..."
	@python3 -m venv .venv
	@. ./.venv/bin/activate
	@echo "Virtual Env Created..."
	@pip3 install -r requirements.txt
	@echo "Please run \". ./.venv/bin/activate\" to start virtual environment"
