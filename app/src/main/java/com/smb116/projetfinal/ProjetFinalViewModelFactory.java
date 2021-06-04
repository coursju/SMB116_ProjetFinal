package com.smb116.projetfinal;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProjetFinalViewModelFactory implements ViewModelProvider.Factory {

    private static ProjetFinalViewModel projetFinalViewModel;
    private final MainActivity mainActivity;

    public ProjetFinalViewModelFactory(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProjetFinalViewModel.class)){
            if (projetFinalViewModel == null){
                projetFinalViewModel = new ViewModelProvider(mainActivity).get(ProjetFinalViewModel.class);
                return (T) projetFinalViewModel;
            }else{
                return (T) projetFinalViewModel;
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public static <T extends ViewModel> T getInstance(@NonNull Class<T> modelClass){
        if (modelClass.isAssignableFrom(ProjetFinalViewModel.class)){
                return (T) projetFinalViewModel;
            }
        return null;
    }
}
