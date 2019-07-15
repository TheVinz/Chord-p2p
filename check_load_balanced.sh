#!/usr/bin/env bash

data_dir=$1

if (( $# != 1 ))
then
    data_dir="target/data"
else
    data_dir=$1
fi

echo "Check folder '$data_dir':"

if [[ -d "$data_dir" ]]
then
    cd "$data_dir"
    du -a | cut -d/ -f2 | sort | uniq -c | sort -nr
else
    echo "Directory does not exist"
    exit 1
fi