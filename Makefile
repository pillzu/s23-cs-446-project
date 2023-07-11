.PHONY: run install-deps
VENV=.venv
PYTHON=$(VENV)/bin/python3
run:
	@. ./.venv/bin/activate
	@echo "Running server listening on local network..."
	@python3 app.py

install-deps:
	@echo "Creating virtual env and installing deps..."
	@python3 -m venv $(VENV)
	@echo "Virtual Env Created! Installing deps..."
	@$(PYTHON) -m pip install -r requirements.txt
	@echo "Please run \". ./.venv/bin/activate\" to start virtual environment"
