git fetch upstream

git checkout master
git reset --hard upstream/master
git push --force origin master

git checkout -B work/patches upstream/master
git am patches/*.patch
git push --force-with-lease origin work/patches
