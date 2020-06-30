#!/bin/sh

# Decrypt the file
mkdir $HOME/cis-android/secrets
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$PASSPHRASE" \
--output $HOME/cis-android/secrets/Christ_key Christ_key.gpg