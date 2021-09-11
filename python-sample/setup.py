# -*- coding: utf-8 -*-

import setuptools

setuptools.setup(
    name='logsloth',
    version='1.0.3',
    description='logsloth.py python logger',
    author='hhd2002',
    author_email='h2d2002@naver.com',
    url='https://github.com/HyundongHwang/logsloth',
    install_requires=[],
    packages=setuptools.find_packages(),
    keywords=['logsloth.py', 'hhd2002', 'utility'],
    python_requires='>=3',
    package_data={},
    zip_safe=False,
    entry_points={
        # 'console_scripts': [
        #     'hhdpy = hhdpy.__main__:main',
        # ]
    },
)

# rm -rf build dist *.egg-info;
# python setup.py bdist_wheel;
# twine upload dist/*.whl;
# ;
# pip install -U hellologsloth;
# pip install -U hellologsloth;
# pip show hellologsloth;
