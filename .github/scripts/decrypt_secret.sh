#!/bin/sh

# Decrypt the file
# mkdir $HOME/secrets
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$KEY_PASSPHRASE" \
--output $HOME/app/google-services.json google-services.json.gpg