import {async, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {CoreComponent} from './core.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {AppComponent} from '../app.component';
import {ErrorComponent} from '../features/error/error.component';
import {CurrentBlockStatusComponent} from '../features/current-block-status/current-block-status.component';
import {StatisticComponent} from '../features/statistic/statistic.component';
import {Blockchain2graphService} from './services/blockchain2graph-service.service';
import {Server} from 'mock-socket';
import {HeaderComponent} from './components/header/header.component';
import {RemainingTimeComponent} from '../features/remaining-time/remaining-time.component';
import {browser} from 'protractor';
import {delay} from 'rxjs/operators';

describe('CoreComponent', () => {

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [
                CoreComponent,
                HeaderComponent,
                AppComponent,
                StatisticComponent,
                CurrentBlockStatusComponent,
                ErrorComponent,
                RemainingTimeComponent],
            providers: [Blockchain2graphService],
            schemas: [CUSTOM_ELEMENTS_SCHEMA]
        }).compileComponents();
    }));

    /**
     * Testing application creation.
     */
    it('should create the app', async(() => {
        const fixture = TestBed.createComponent(CoreComponent);
        const app = fixture.debugElement.componentInstance;
        expect(app).toBeTruthy();
    }));

    /**
     * Testing that the statics blocks are created with the correct parameters.
     */
    it('Should have statistic blocks', async(() => {
        const fixture = TestBed.createComponent(CoreComponent);
        fixture.detectChanges();
        const compiled = fixture.debugElement.nativeElement;

        // All icons are set.
        expect(compiled.querySelector('.fa.fa-question')).toBeNull();

        // Number of blocks in bitcoin core.
        expect(compiled.querySelector('.fa.fa-server')).not.toBeNull();
        expect(compiled.querySelectorAll('h4')[0].textContent).toContain('Blocks in bitcoin core');
        expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');

        // Number of blocks in neo4j.
        expect(compiled.querySelector('.fa.fa-database')).not.toBeNull();
        expect(compiled.querySelectorAll('h4')[1].textContent).toContain('Blocks in neo4j');
        expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');

        // Average block import duration.
        expect(compiled.querySelector('.fa.fa-hourglass-half')).not.toBeNull();
        expect(compiled.querySelectorAll('h4')[2].textContent).toContain('Block import duration');
        expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');
    }));

    /**
     * Testing that server message update statistics blocks.
     */
    it('Should update statistics blocks', async(() => {
            const webSocketURL = 'ws://' + location.host + '/' + Blockchain2graphService.webSocketPath;
            const mockServer = new Server(webSocketURL);
            const fixture = TestBed.createComponent(CoreComponent);
            let message;
            fixture.detectChanges();
            const compiled = fixture.debugElement.nativeElement;

            // Checking that we have n/a.
            expect(compiled.querySelectorAll('h4')[0].textContent).toContain('Blocks in bitcoin core');
            expect(compiled.querySelectorAll('h4')[1].textContent).toContain('Blocks in neo4j');
            expect(compiled.querySelectorAll('h4')[2].textContent).toContain('Block import duration');
            expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
            expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
            expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

            // Updating values.
            message = {
                blockCountInBlockchain: 2000,
                blockCountInNeo4j: 1000,
                currentBlockStatus: {
                    blockHeight: -1,
                    processStep: 'NOTHING_TO_PROCESS',
                    processedAddresses: -1,
                    addressCount: -1,
                    processedTransactions: -1,
                    transactionCount: -1
                },
                lastBlockProcessDuration: 17454,
                lastErrorMessage: 'n/a'
            };

            mockServer.emit('message', JSON.stringify(message));
            fixture.detectChanges();
            expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2 000');
            expect(compiled.querySelectorAll('h5')[1].textContent).toContain('1 000');
            expect(compiled.querySelectorAll('h5')[2].textContent).toContain('17.45 s');

            // Sending a message with nothing to see that we still display nothing.
            message = {
                blockCountInBlockchain: -1,
                blockCountInNeo4j: -1,
                currentBlockStatus: {
                    blockHeight: -1,
                    processStep: 'NOTHING_TO_PROCESS',
                    processedAddresses: -1,
                    addressCount: -1,
                    processedTransactions: -1,
                    transactionCount: -1
                },
                lastBlockProcessDuration: -1.0,
                lastErrorMessage: 'n/a'
            };
            mockServer.emit('message', JSON.stringify(message));
            fixture.detectChanges();
            expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
            expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
            expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

            // Changing the blocks count in bitcoin core.
            message = {
                blockCountInBlockchain: 2,
                blockCountInNeo4j: 3,
                currentBlockStatus: {
                    blockHeight: -1,
                    processStep: 'NOTHING_TO_PROCESS',
                    processedAddresses: -1,
                    addressCount: -1,
                    processedTransactions: -1,
                    transactionCount: -1
                },
                lastBlockProcessDuration: -1.0,
                lastErrorMessage: 'n/a'
            };
            mockServer.emit('message', JSON.stringify(message));
            fixture.detectChanges();
            expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2');
            expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3');
            expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

            // Changing the average block import duration.
            message = {
                blockCountInBlockchain: 2,
                blockCountInNeo4j: 3,
                currentBlockStatus: {
                    blockHeight: -1,
                    processStep: 'NOTHING_TO_PROCESS',
                    processedAddresses: -1,
                    addressCount: -1,
                    processedTransactions: -1,
                    transactionCount: -1
                },
                lastBlockProcessDuration: 2300,
                lastErrorMessage: 'n/a'
            };
            mockServer.emit('message', JSON.stringify(message));
            fixture.detectChanges();
            expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2');
            expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3');
            expect(compiled.querySelectorAll('h5')[2].textContent).toContain('2.30 s');

            mockServer.close();
        })
    );

    /**
     * Testing the statistic blocks update from the server.
     */
    it('Should update statistic blocks', async(() => {
        const webSocketURL = 'ws://' + location.host + '/' + Blockchain2graphService.webSocketPath;
        const mockServer = new Server(webSocketURL);
        const fixture = TestBed.createComponent(CoreComponent);
        let message;
        fixture.detectChanges();
        const compiled = fixture.debugElement.nativeElement;

        // Checking that we have nothing.
        expect(compiled.querySelectorAll('h4')[3].textContent).toContain('No block to process');
        expect(compiled.querySelector('div.progress-bar')).toBeNull();

        // Sending a message with nothing to see.
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: -1,
                processStep: 'NOTHING_TO_PROCESS',
                processedAddresses: -1,
                addressCount: -1,
                processedTransactions: -1,
                transactionCount: -1
            },
            averageBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[3].textContent).toContain('No block to process');
        expect(compiled.querySelector('div.progress-bar')).toBeNull();

        // Sending a message of a new block to process (NEW_BLOCK_TO_PROCESS).
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'NEW_BLOCK_TO_PROCESS',
                loadedTransactions: -1,
                processedAddresses: -1,
                addressCount: -1,
                processedTransactions: -1,
                transactionCount: -1
            },
            averageBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
        expect(compiled.querySelector('div.progress-bar')).not.toBeNull();

        // Sending a message of loading block transactions (LOADING_TRANSACTIONS_FROM_BLOCKCHAIN).
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'LOADING_TRANSACTIONS_FROM_BLOCKCHAIN',
                loadedTransactions: 200,
                processedAddresses: -1,
                addressCount: -1,
                processedTransactions: -1,
                transactionCount: 1000
            },
            averageBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
        expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Loading transactions from blockchain...');
        expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
        expect(compiled.querySelector('div.progress-bar').textContent).toContain('20 %');

        // Sending a message of processing addresses (PROCESSING_ADDRESSES).
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'PROCESSING_ADDRESSES',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: -1,
                transactionCount: 1000
            },
            averageBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
        expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Processing addresses...');
        expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
        expect(compiled.querySelector('div.progress-bar').textContent).toContain('25 %');

        // Sending a message of processing transactions (PROCESSING_TRANSACTIONS).
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'PROCESSING_TRANSACTIONS',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: 351,
                transactionCount: 1000
            },
            averageBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
        expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Processing transactions...');
        expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
        expect(compiled.querySelector('div.progress-bar').textContent).toContain('35 %');

        // Sending a message of saving block (SAVING_BLOCK).
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'SAVING_BLOCK',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: 351,
                transactionCount: 1000
            },
            averageBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
        expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Saving block...');
        expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
        expect(compiled.querySelector('div.progress-bar').textContent).toContain('100 %');

        // Sending a message of block saved (BLOCK_SAVED).
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'BLOCK_SAVED',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: 351,
                transactionCount: 1000
            },
            averageBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
        expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Block saved');
        expect(compiled.querySelector('div.progress-bar')).not.toBeNull();

        mockServer.close();
    }));

    /**
     * Testing the display of errors.
     */
    it('Should display errors', async(() => {
        const webSocketURL = 'ws://' + location.host + '/' + Blockchain2graphService.webSocketPath;
        const mockServer = new Server(webSocketURL);
        const fixture = TestBed.createComponent(CoreComponent);
        let message;
        fixture.detectChanges();
        const compiled = fixture.debugElement.nativeElement;

        // Checking that we have no error by default.
        expect(compiled.querySelector('div.card-header.bg-danger')).toBeNull();
        expect(compiled.querySelector('div.body.bg-danger')).toBeNull();

        // Sending a message with nothing to see and check that there is no error.
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: -1,
                processStep: 'NOTHING_TO_PROCESS',
                processedAddresses: -1,
                addressCount: -1,
                processedTransactions: -1,
                transactionCount: -1
            },
            lastBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelector('div.bg-danger')).toBeNull();

        // Sending an error message.
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: -1,
                processStep: 'NOTHING_TO_PROCESS',
                processedAddresses: -1,
                addressCount: -1,
                processedTransactions: -1,
                transactionCount: -1
            },
            lastBlockProcessDuration: -1.0,
            lastErrorMessage: 'This is an error message'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelector('div.bg-danger')[0]).not.toBeNull();
        expect(compiled.querySelector('div.bg-danger')[1]).not.toBeNull();
        expect(compiled.querySelectorAll('h5')[4].textContent).toContain('This is an error message');

        mockServer.close();
    }));


    /**
     * Testing the display of errors.
     */
    it('Should display remaining time', fakeAsync(() => {
        const webSocketURL = 'ws://' + location.host + '/' + Blockchain2graphService.webSocketPath;
        const mockServer = new Server(webSocketURL);
        const fixture = TestBed.createComponent(CoreComponent);
        let message;
        fixture.detectChanges();
        const compiled = fixture.debugElement.nativeElement;

        // Sending a message with no information.
        message = {
            blockCountInBlockchain: -1,
            blockCountInNeo4j: -1,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'BLOCK_SAVED',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: 351,
                transactionCount: 1000
            },
            averageBlockProcessDuration: -1.0,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[4].textContent).toContain('Not available for the moment');

        // Sending a message saying 100 000 blocks are missing and it takes 865 to treat a block. Takes a bit more than one day.
        message = {
            blockCountInBlockchain: 500000,
            blockCountInNeo4j: 400000,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'BLOCK_SAVED',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: 351,
                transactionCount: 1000
            },
            lastBlockProcessDuration: 865,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[4].textContent).toContain('2 day(s) remaining');

        // Sending new information just after should not update the statistics.
        message = {
            blockCountInBlockchain: 500000,
            blockCountInNeo4j: 400000,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'BLOCK_SAVED',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: 351,
                transactionCount: 1000
            },
            lastBlockProcessDuration: 60000,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[4].textContent).toContain('2 day(s) remaining');

        // Waiting for one minute.
        tick(61 * 1000);

        // Sending a message saying 100 000 blocks are missing and it takes 865 to treat a block. Takes a bit more than one day.
        message = {
            blockCountInBlockchain: 500000,
            blockCountInNeo4j: 400000,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'BLOCK_SAVED',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: 351,
                transactionCount: 1000
            },
            lastBlockProcessDuration: 10000,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[4].textContent).toContain('12 day(s) remaining');

        // Waiting for one minute.
        tick(61 * 1000);

        // Sending a message saying that all is synchronized.
        message = {
            blockCountInBlockchain: 500000,
            blockCountInNeo4j: 499999,
            currentBlockStatus: {
                blockHeight: 10,
                processStep: 'BLOCK_SAVED',
                loadedTransactions: 200,
                processedAddresses: 25,
                addressCount: 100,
                processedTransactions: 351,
                transactionCount: 1000
            },
            lastBlockProcessDuration: 10000,
            lastErrorMessage: 'n/a'
        };
        mockServer.emit('message', JSON.stringify(message));
        fixture.detectChanges();
        expect(compiled.querySelectorAll('h4')[4].textContent).toContain('Synchronized');

        mockServer.close();
    }));

});
