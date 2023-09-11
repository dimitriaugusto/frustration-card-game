package com.dimiaug.frustration.features.play.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.dimiaug.frustration.features.play.gateways.data.PlayEntity;
import com.dimiaug.frustration.features.play.gateways.repo.PlayRepository;
import com.dimiaug.frustration.features.play.domain.logic.GameTable;
import com.dimiaug.frustration.features.play.domain.model.Play;
import com.dimiaug.frustration.features.play.domain.model.Summary;

import java.util.List;
import java.util.stream.Collectors;

public class GameTableViewModel extends AndroidViewModel {

    private final PlayRepository mPlayRepository;

    private final MediatorLiveData<List<Play>> mObservablePlays = new MediatorLiveData<>();
    private final MediatorLiveData<List<Summary>> mObservableSummaries = new MediatorLiveData<>();

    private GameTable mGameTable;

    public GameTableViewModel(@NonNull Application application, PlayRepository playRepository) {
        super(application);

        mPlayRepository = playRepository;

        initLogic();
        initMediators();
    }

    private void initLogic() {
        mGameTable = new GameTable(getApplication());
    }

    private void initMediators() {
        mObservablePlays.addSource(mPlayRepository.getPlays(),
                playEntities -> {
                    mObservablePlays.postValue(toPlays(playEntities));
                    mObservableSummaries.postValue(updateGameTable(playEntities));
                }
        );
    }

    private List<Play> toPlays(List<PlayEntity> playEntities) {
        return playEntities.stream().map(Play::new).collect(Collectors.toList());
    }

    private List<Summary> updateGameTable(List<PlayEntity> playEntities) {
        playEntities.forEach((playEntity) -> mGameTable.put(new Play(playEntity)));
        return mGameTable.getPlayersSummaries();
    }

    public LiveData<List<Play>> getAllPlays() {
        return mObservablePlays;
    }

    public LiveData<List<Summary>> getAllSummaries() {
        return mObservableSummaries;
    }

    public void addPlay(Play play) {
        mGameTable.put(play);
        mPlayRepository.insertPlay(new PlayEntity(play.getPlayer(), play.getRound(), play.getPoints()));
    }

    public void updatePlay(Play play) {
        mGameTable.edit(play);
        mPlayRepository.updatePlay(new PlayEntity(play.getPlayer(), play.getRound(), play.getPoints()));
    }

    public void clearGame() {
        initLogic();
        mPlayRepository.deleteAllPlays();
    }

    public boolean isGameFinished() {
        return mGameTable.isGameFinished();
    }

    public Play getNextPlay() {
        return mGameTable.getNextPlay();
    }
}