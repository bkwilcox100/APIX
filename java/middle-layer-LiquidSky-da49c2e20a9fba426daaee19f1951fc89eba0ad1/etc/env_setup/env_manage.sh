#!/bin/bash

clear
echo "Enter project ID"
read proj_id

echo "Enter project name"
read name

echo "Project ID: $proj_id; Project Name: $name; Project Type: $type"

echo gcloud alpha projects create $proj_id --name="$name" --labels=type=$type
gcloud alpha projects create $proj_id --name="$name"

