import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.datasets import fetch_20newsgroups

ip = fetch_20newsgroups(subset='train').data

tfidf = TfidfVectorizer()
df = tfidf.fit_transform(ip)

np.savetxt('tfidf.tsv',df.todense(),delimiter='\t')
