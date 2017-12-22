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