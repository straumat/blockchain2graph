import {Injectable, OnDestroy} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {
    ApplicationStatus,
    CurrentBlockStatus,
    CurrentBlockStatusProcessStep
} from '../../shared/blockchain2graph-bitcoin-neo4j-back-end';

@Injectable({
    providedIn: 'root'
})
export class Blockchain2graphService implements OnDestroy {

    // Static values.
    static readonly nonAvailableValueNumber = -1;
    static readonly nonAvailableValueString = 'n/a';

    // Websocket connexion.
    static readonly webSocketPath = '/status/websocket';
    private webSocket: WebSocket;

    // Status values.
    private readonly blockCountInBlockchainSubject: BehaviorSubject<number>;
    private readonly blockCountInNeo4jSubject: BehaviorSubject<number>;
    private readonly lastBlockProcessDurationSubject: BehaviorSubject<number>;
    private readonly currentBlockStatusSubject: BehaviorSubject<CurrentBlockStatus>;
    private readonly lastErrorMessageSubject: BehaviorSubject<string>;

    // Observable of status values.
    public readonly blockCountInBlockchain: Observable<number>;
    public readonly blockCountInNeo4j: Observable<number>;
    public readonly lastBlockProcessDuration: Observable<number>;
    public readonly currentBlockStatus: Observable<CurrentBlockStatus>;
    public readonly lastErrorMessage: Observable<string>;

    constructor() {
        // Instantiate a current block state saying nothing has been done yet.
        const nonAvailableBlockStatusValue = {} as CurrentBlockStatus;
        nonAvailableBlockStatusValue.blockHeight = Blockchain2graphService.nonAvailableValueNumber;
        nonAvailableBlockStatusValue.processStep = CurrentBlockStatusProcessStep.NO_BLOCK_TO_PROCESS;
        nonAvailableBlockStatusValue.transactionCount = Blockchain2graphService.nonAvailableValueNumber;
        nonAvailableBlockStatusValue.addressCount = Blockchain2graphService.nonAvailableValueNumber;
        nonAvailableBlockStatusValue.loadedTransactions = Blockchain2graphService.nonAvailableValueNumber;
        nonAvailableBlockStatusValue.processedAddresses = Blockchain2graphService.nonAvailableValueNumber;
        nonAvailableBlockStatusValue.processedTransactions = Blockchain2graphService.nonAvailableValueNumber;

        // Initiate subjects.
        this.blockCountInBlockchainSubject = new BehaviorSubject<number>(Blockchain2graphService.nonAvailableValueNumber);
        this.blockCountInNeo4jSubject = new BehaviorSubject<number>(Blockchain2graphService.nonAvailableValueNumber);
        this.lastBlockProcessDurationSubject = new BehaviorSubject<number>(Blockchain2graphService.nonAvailableValueNumber);
        this.currentBlockStatusSubject = new BehaviorSubject<CurrentBlockStatus>(nonAvailableBlockStatusValue);
        this.lastErrorMessageSubject = new BehaviorSubject<string>(Blockchain2graphService.nonAvailableValueString);

        // Initiate observables.
        this.blockCountInBlockchain = this.blockCountInBlockchainSubject.asObservable();
        this.blockCountInNeo4j = this.blockCountInNeo4jSubject.asObservable();
        this.lastBlockProcessDuration = this.lastBlockProcessDurationSubject.asObservable();
        this.currentBlockStatus = this.currentBlockStatusSubject.asObservable();
        this.lastErrorMessage = this.lastErrorMessageSubject.asObservable();

        // Connecting and subscribing to the websocket.
        const webSocketURL = 'ws://' + location.host + '/' + Blockchain2graphService.webSocketPath;
        this.webSocket = new WebSocket(webSocketURL);
        this.webSocket.addEventListener('message', message => {
            this.processMessage(JSON.parse(message.data));
        });
    }

    /**
     * Triggered when a new message is coming from the server.
     */
    public processMessage(message: ApplicationStatus) {
        // blockCountInBlockchainSubject.
        if (message.blockCountInBlockchain !== this.blockCountInBlockchainSubject.getValue()) {
            this.blockCountInBlockchainSubject.next(message.blockCountInBlockchain);
        }

        // blocksCountInNeo4j.
        if (message.blockCountInNeo4j !== this.blockCountInNeo4jSubject.getValue()) {
            this.blockCountInNeo4jSubject.next(message.blockCountInNeo4j);
        }

        // lastBlockProcessDuration.
        if (message.lastBlockProcessDuration !== this.lastBlockProcessDurationSubject.getValue()) {
            this.lastBlockProcessDurationSubject.next(message.lastBlockProcessDuration);
        }

        // currentBlockStatus.
        if (this.isCurrentBlockStatusDifferent(message.currentBlockStatus)) {
            this.currentBlockStatusSubject.next(message.currentBlockStatus);
        }

        // lastErrorMessage.
        if (message.lastErrorMessage !== this.lastErrorMessageSubject.getValue()) {
            this.lastErrorMessageSubject.next((message.lastErrorMessage));
        }
    }

    /**
     * Returns true if the block status set as parameter is different from the one we previously had.
     */
    private isCurrentBlockStatusDifferent(c: CurrentBlockStatus) {
        return this.currentBlockStatusSubject.getValue() == null ||
            this.currentBlockStatusSubject.getValue().blockHeight !== c.blockHeight ||
            this.currentBlockStatusSubject.getValue().processStep !== c.processStep ||
            this.currentBlockStatusSubject.getValue().transactionCount !== c.transactionCount ||
            this.currentBlockStatusSubject.getValue().addressCount !== c.addressCount ||
            this.currentBlockStatusSubject.getValue().loadedTransactions !== c.loadedTransactions ||
            this.currentBlockStatusSubject.getValue().processedAddresses !== c.processedAddresses ||
            this.currentBlockStatusSubject.getValue().processedTransactions !== c.processedTransactions;
    }

    /**
     * On close, we disconnect from the server.
     */
    ngOnDestroy(): void {
        this.webSocket.close();
    }

}
