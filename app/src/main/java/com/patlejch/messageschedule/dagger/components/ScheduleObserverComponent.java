package com.patlejch.messageschedule.dagger.components;

import com.patlejch.messageschedule.alarm.ScheduleFileObserver;
import com.patlejch.messageschedule.dagger.scopes.ApplicationScope;

import dagger.Component;

@Component(dependencies = SingletonComponent.class)
@ApplicationScope
public interface ScheduleObserverComponent {
    ScheduleFileObserver observer();
}
