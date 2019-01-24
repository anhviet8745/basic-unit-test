package com.example.anhvietpham.basic_unit_test.testdrivendevelopment.pingserver

import com.example.anhvietpham.basic_unit_test.testdrivendevelopment.pingserver.network.PingServerHttpEndPointSync

class PingServerSyncUseCase(private val mPingServerHttpEndPointSync: PingServerHttpEndPointSync) {

    fun pingServerSync(): UseCaseResult {
        val result = mPingServerHttpEndPointSync.pingServerSync()
        return if (result == PingServerHttpEndPointSync.EndPointResult.GENERAL_ERROR) {
            UseCaseResult.FAILURE
        } else if (result == PingServerHttpEndPointSync.EndPointResult.NETWORK_ERROR) {
            UseCaseResult.FAILURE
        } else {
            UseCaseResult.SUCCESS
        }
    }

    enum class UseCaseResult {
        SUCCESS,
        FAILURE
    }
}