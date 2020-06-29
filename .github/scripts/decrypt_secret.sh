#!/bin/sh

gpg --quiet --batch --yes --decrypt --passphrase="$PASSPHRASE" \
--output $HOME/app/Christ_key Christ_key.gpg