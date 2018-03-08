from django.urls import path

from . import views

urlpatterns = [
	path('', views.index, name='index'),
	path('api/', views.api, name='api'),
	path('addMetadata/', views.parse),
	path('editMetadata/', views.api),
	path('metadataInfo/', views.api),
	path('endpoint/', views.endpoint, name='endpoint'),
]