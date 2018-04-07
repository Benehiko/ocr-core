#!/bin/bash
#Simple script to pull compiled jar and lib
echo "Enter github repository URL"
read $github
echo "Enter branch to clone"
read $branch

git clone -b $branch --single-branch $github 
echo "Fetched compiled jar and lib"
