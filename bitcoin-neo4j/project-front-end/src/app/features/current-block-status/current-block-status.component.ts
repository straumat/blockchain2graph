import {Component, OnInit} from '@angular/core';
import {Blockchain2graphService} from '../../core/services/blockchain2graph-service.service';
import {CurrentBlockStatus, CurrentBlockStatusProcessStep} from '../../shared/blockchain2graph-bitcoin-neo4j-back-end';
import {faBolt} from '@fortawesome/free-solid-svg-icons';

@Component({
    selector: 'app-current-block-status',
    templateUrl: './current-block-status.component.html',
    styleUrls: ['./current-block-status.component.css']
})
export class CurrentBlockStatusComponent implements OnInit {

    // Static values.
    static readonly blockHeightNonAvailable = -1;
    static readonly noBlockToProcessDescription = 'No block to process';
    static readonly newBlockToProcessDescription = 'New block to process';
    static readonly loadingTransactionsFromBlockchainDescription = 'Loading transactions from blockchain...';
    static readonly processingAddressesDescription = 'Processing addresses...';
    static readonly processingTransactionsDescription = 'Processing transactions...';
    static readonly savingBlockDescription = 'Saving block...';
    static readonly savedBlockDescription = 'Block saved';

    // Component details.
    faBolt = faBolt;
    blockHeight = CurrentBlockStatusComponent.noBlockToProcessDescription;
    viewDetails = false;
    processStepDescription = CurrentBlockStatusComponent.noBlockToProcessDescription;
    progression = 0;

    /**
     * Constructor.
     */
    constructor(private blockchain2graphService: Blockchain2graphService) {
    }

    /**
     * Subscribe to block change status.
     */
    ngOnInit() {
        // We subscribe to the change of block status.
        this.blockchain2graphService.currentBlockStatus.subscribe((blockStatus: CurrentBlockStatus) => {
            this.processStatus(blockStatus);
        });
    }

    /**
     * Process the new block status.
     */
    private processStatus(blockStatus: CurrentBlockStatus) {
        this.setBlockHeight(blockStatus.blockHeight);
        switch (blockStatus.processStep) {

            // Nothing to process.
            case CurrentBlockStatusProcessStep.NO_BLOCK_TO_PROCESS:
                this.processStepDescription = CurrentBlockStatusComponent.noBlockToProcessDescription;
                this.viewDetails = false;
                break;

            // New block to process.
            case CurrentBlockStatusProcessStep.NEW_BLOCK_TO_PROCESS:
                this.processStepDescription = CurrentBlockStatusComponent.newBlockToProcessDescription;
                this.viewDetails = true;
                break;

            // Loading transactions from bitcoin core.
            case CurrentBlockStatusProcessStep.LOADING_TRANSACTIONS_FROM_BLOCKCHAIN:
                this.processStepDescription = CurrentBlockStatusComponent.loadingTransactionsFromBlockchainDescription;
                this.viewDetails = true;
                this.setProgression(blockStatus.loadedTransactions, blockStatus.transactionCount);
                break;

            // Processing addresses.
            case CurrentBlockStatusProcessStep.PROCESSING_ADDRESSES:
                this.processStepDescription = CurrentBlockStatusComponent.processingAddressesDescription;
                this.viewDetails = true;
                this.setProgression(blockStatus.processedAddresses, blockStatus.addressCount);
                break;

            // Processing transactions.
            case CurrentBlockStatusProcessStep.PROCESSING_TRANSACTIONS:
                this.processStepDescription = CurrentBlockStatusComponent.processingTransactionsDescription;
                this.viewDetails = true;
                this.setProgression(blockStatus.processedTransactions, blockStatus.transactionCount);
                break;

            // Saving block.
            case CurrentBlockStatusProcessStep.SAVING_BLOCK:
                this.processStepDescription = CurrentBlockStatusComponent.savingBlockDescription;
                this.viewDetails = true;
                this.setProgression(100, 100);
                break;

            // Block saved.
            case CurrentBlockStatusProcessStep.BLOCK_SAVED:
                this.processStepDescription = CurrentBlockStatusComponent.savedBlockDescription;
                this.viewDetails = true;
                this.setProgression(100, 100);
                break;
        }
    }

    /**
     * Sets block height as a string and with 8 characters (leading 0).
     */
    public setBlockHeight(height: number) {
        if (height === CurrentBlockStatusComponent.blockHeightNonAvailable) {
            // Nothing.
            this.blockHeight = CurrentBlockStatusComponent.noBlockToProcessDescription;
        } else {// if (height !== null) {
            // Format block height.
            this.blockHeight = 'Block ' + height.toString().padStart(8, '0');
        }
    }

    /**
     * Set the progression bar.
     */
    public setProgression(current: number, total: number) {
        if (total !== 0) {
            const value = Math.trunc((current / total) * 100);
            if (value < 0) {
                this.progression = 0;
            } else {
                this.progression = value;
            }
        } else {
            this.progression = 0;
        }
    }

}
