import LinkHelperGenerator from './LinkHelperGenerator';

describe('getLabelText', () => {
    test('without val element', () => {
        const linkhelper = new LinkHelperGenerator({});
        expect(linkhelper.getLabelText('association', 'text_id')).toEqual('');
    });

    test('with val element', () => {
        const linkhelper = new LinkHelperGenerator({
            model: {
                edges: [
                    {
                        connection: {
                            vals: [
                                {
                                    key: 'text_id',
                                    value: 'label',
                                }
                            ]
                        },
                        name: 'association',
                    }
                ]
            }
        });
        expect(linkhelper.getLabelText('association', 'text_id')).toEqual('label');
    });
});

describe('mapping', () => {
    test('without var element', () => {
        const linkhelper = new LinkHelperGenerator({});
        expect(linkhelper.mapping).toEqual({});
    });

    test('with var element', () => {
        const linkhelper = new LinkHelperGenerator({
            model: {
                edges: [
                    {
                        connection: {
                            vars: [
                                {
                                    key: 'text_id',
                                    value: 'model_attribute',
                                }
                            ]
                        },
                        mReference: 'Association',
                    }
                ]
            }
        });
        expect(linkhelper.mapping).toEqual({
            'Association': {
                'text_id': 'model_attribute'
            }
        });
    });
});