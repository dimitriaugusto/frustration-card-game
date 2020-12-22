package com.dimilo.frustration.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.dimilo.frustration.FrustrationApp;
import com.dimilo.frustration.data.PlayEntity;
import com.dimilo.frustration.logic.GameTable;
import com.dimilo.frustration.model.Play;
import com.dimilo.frustration.model.Summary;
import com.dimilo.frustration.repo.AppRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GameTableViewModel extends AndroidViewModel {

    private final AppRepository mAppRepository;

    private final MediatorLiveData<List<Play>> mObservablePlays = new MediatorLiveData<>();
    private final MediatorLiveData<List<Summary>> mObservableSummaries = new MediatorLiveData<>();

    private GameTable mGameTable;

    public GameTableViewModel(@NonNull Application application) {
        super(application);

        initLogic();
        mAppRepository = ((FrustrationApp) getApplication()).getRepository();
        initMediators();
    }

    private void initLogic() {
        mGameTable = new GameTable(getApplication());
    }

    boolean loaded = false;

    private void initMediators() {
        mObservablePlays.addSource(mAppRepository.getPlays(),
                playEntities -> {
                    if (!loaded) {
                        mObservablePlays.postValue(toPlays(playEntities));
                        mObservableSummaries.postValue(initGameTable(playEntities));
                        loaded = true;
                    }
                }
        );
    }

    private List<Play> toPlays(List<PlayEntity> playEntities) {
        return playEntities.stream().map(Play::new).collect(Collectors.toList());
    }

    private List<Summary> initGameTable(List<PlayEntity> playEntities) {
        playEntities.forEach((playEntity) -> mGameTable.put(new Play(playEntity)));
        return mGameTable.getPlayersSummaries();
    }

    public LiveData<List<Play>> getAllPlays() {
        return mObservablePlays;
    }

    public LiveData<List<Summary>> getAllSummaries() {
        return mObservableSummaries;
    }

    public Summary addPlay(Play play) {
        mAppRepository.insertPlay(new PlayEntity(play.getPlayer(), play.getRound(), play.getPoints()));
        return mGameTable.put(play);
    }

    public Summary updatePlay(Play play) {
        mAppRepository.updatePlay(new PlayEntity(play.getPlayer(), play.getRound(), play.getPoints()));
        return mGameTable.edit(play);
    }

    public void clearGame() {
        mAppRepository.clearGame();
        initLogic();
    }

    public boolean isGameFinished() {
        return mGameTable.isGameFinished();
    }

    public Play getNextPlay() {
        return mGameTable.getNextPlay();
    }
}
